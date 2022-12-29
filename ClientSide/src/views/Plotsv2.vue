<template>
  <div>
    <Row>
      <Col span="12" offset="6">
        <v-chart class="chart" :option="paretoOptionAll" @click="paretoChartClicked"/>
      </Col>
    </Row>
    <Row>
      <Col span="16" offset="4">
        <div id="map" ref="rootmap"></div>
      </Col>
    </Row>
    <br>
    <Row>
      <Col span="16" offset="4">
        <v-chart ref="path3DChart" class="chartBig" :option="path3DOption" @click="path3DClicked"/>
      </Col>
    </Row>
    <br>
    <Row>
      <Col span="16" offset="4">
        <v-chart class="chart" :option="hypervolumeOption"/>
      </Col>
    </Row>
    <br>
    <!--    <Row>-->
    <!--      <Col span="16" offset="4">-->
    <!--        <v-chart ref="path3DReductionRateChart" class="chartBig" :option="path3DReductionRateOption" @click="path3DReductionRateClicked"/>-->
    <!--      </Col>-->
    <!--    </Row>-->
  </div>
</template>

<script>
import 'echarts-gl'
// import { use } from 'echarts/core'
import * as echarts from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { ScatterChart, LineChart } from 'echarts/charts'
import {
  ToolboxComponent,
  TitleComponent,
  TooltipComponent,
  LegendComponent, GridComponent
} from 'echarts/components'

import { Line3DChart } from 'echarts-gl/charts'
import { Grid3DComponent } from 'echarts-gl/components'

import 'ol/ol.css'
import View from 'ol/View'
import Map from 'ol/Map'
import * as olProj from 'ol/proj'
import GeoJSON from 'ol/format/GeoJSON'
import VectorLayer from 'ol/layer/Vector'
import TileLayer from 'ol/layer/Tile'
// import OSM from 'ol/source/OSM'
import BingMaps from 'ol/source/BingMaps'
import VectorSource from 'ol/source/Vector'
import { Style, Fill, Stroke, Text } from 'ol/style'
import { ScaleLine } from 'ol/control'
// ol-ext
import 'ol-ext/dist/ol-ext.css'
import ol_control_Print from 'ol-ext/control/Print'
import ol_control_CanvasScaleLine from 'ol-ext/control/CanvasScaleLine'
import { saveAs } from 'file-saver'

import axios from 'axios'

echarts.use([
  CanvasRenderer,
  ScatterChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  ToolboxComponent,
  LegendComponent,
  GridComponent,
  Line3DChart,
  Grid3DComponent
])
export default {
  name: 'Plots',
  data () {
    return {
      map: null,
      spUnitVectorLayer: null,
      styleSeqCollection: [],
      paretoFrontsOrigin: {},//store a copy of original data
      paretoOptionAll: {},
      hypervolumeOption: {},
      path3DOption: {},
      seriesNames: ['ONE+VARY', 'STEP+VARY', 'STEP+FIXED', 'ONE+FIXED'],

      toolbox: {
        feature: {
          saveAsImage: {
            type: 'png',
            pixelRatio: 5
          }
        }
      },
      highlightSeries: null,
      normalLineStyle: {
        opacity: 0.4,
        width: 1
      },
      highlightLineStyle: {
        opacity: 1,
        width: 5
      },
      unit_to_gene: {},
      downloadUrl: ''
    }
  },
  mounted () {
    // console.log(this.$refs.path3DChart)
    this.getParetoFronts()
    this.getUnitToGeneIndx()
    this.getHypervolum()
    this.buildStyleSeqCollection()
    this.prepareMap()
  },
  methods: {
    getScenarioByID (sceid) {
      let selectedScenario = null
      let found = false
      for (let front of this.paretoFrontsOrigin) {
        for (let scenario of front) {
          if (scenario['scenarioID'] == sceid) {
            selectedScenario = scenario
            found = true
            break
          }
        }
        if (found) break
      }
      if (!found) {
        console.log('not found the selected scenario')
        return null
      }
      return selectedScenario
    },
    showScenarioOnMap (scenario) {
      let root = this
      this.spUnitVectorLayer.setStyle(function (feature, res) {
        let slpposUnit = feature.values_['DN']
        let geneIndex = root.unit_to_gene_indx[slpposUnit]//slppos unit id => gene index
        let geneValue = scenario.geneValues[geneIndex]
        // console.log(slpposUnit, gene_indx, geneValue)
        if (geneValue === 0) {
          return root.styleSeqCollection[0][0]
        } else {
          let bmpType = Math.floor(geneValue / 1000)
          let implYear = geneValue % 1000
          return root.styleSeqCollection[bmpType][5 - implYear]
        }
      })
    },
    paretoChartClicked (params) {
      // get scenario id
      const sceid = params.data[2]
      let selectedScenario = this.getScenarioByID(sceid)
      // show selectedScenario on the map
      this.showScenarioOnMap(selectedScenario)
      //highlight scenario path in 3D chart
      let selected3DSeriesIndex = -1
      for (let i = 0; i < this.path3DOption.series.length; i++) {
        let temp = this.path3DOption.series[i]
        if (temp.type !== 'line3D') continue
        if (temp.data[0][3] === selectedScenario['scenarioID']) {
          selected3DSeriesIndex = i
          break
        }
      }
      if (this.highlightSeries != null) {
        this.clearHighlightSeries()
      }
      console.log(this.path3DOption.series[selected3DSeriesIndex])
      this.highlightSeries = this.path3DOption.series[selected3DSeriesIndex]
      this.highlightSeries.lineStyle = this.highlightLineStyle
    },
    clearHighlightSeries () {
      this.highlightSeries.lineStyle = this.normalLineStyle
      // this.$refs.path3DChart.setOption(this.path3DOption)
      this.highlightSeries = null
    },
    path3DClicked (params) {
      if (this.highlightSeries != null) {
        this.clearHighlightSeries()
      }
      console.log(this.path3DOption.series[params.seriesIndex])
      this.highlightSeries = this.path3DOption.series[params.seriesIndex]
      this.highlightSeries.lineStyle = this.highlightLineStyle
      // this.$refs.path3DChart.setOption(this.path3DOption)

      // get scenario id
      const sceid = params.data[3]
      let selectedScenario = this.getScenarioByID(sceid)
      // show selectedScenario on the map
      this.showScenarioOnMap(selectedScenario)
      //highlight point in scatter plot

    },
    getHypervolum () {
      const root = this
      axios.get('http://localhost:8081/hypervolumeAll')
        .then(function (response) {
          const hypervolumes = []
          for (const hvArray of response.data) {
            const hypervolum = []
            for (const hv of hvArray) {
              hypervolum.push([hv.gen, hv.hypervolume])
            }
            hypervolumes.push(hypervolum)
          }
          // console.log(hypervolumes)
          root.hypervolumeOption = {
            // title: {
            //   // text: 'Hypervolume曲线',
            //   left: 'center',
            //   padding: 20
            // },
            grid: {},
            xAxis: {
              type: 'value',
              axisLine: {
                lineStyle: {
                  color: 'rgb(0,0,0)'
                }
              },
              axisLabel: {
                fontSize: 14
              },
              name: 'Generation',
              nameLocation: 'center',
              nameGap: 30,
              nameTextStyle: {
                fontWeight: 'bold',
                color: 'rgb(0,0,0)',
                fontSize: 18
              },
              scale: true,
            },
            yAxis: {
              type: 'value',
              axisLine: {
                lineStyle: {
                  color: 'rgb(0,0,0)'
                }
              },
              axisLabel: {
                fontSize: 14
              },
              name: 'Hypervolume index',
              nameLocation: 'center',
              nameGap: 40,
              nameTextStyle: {
                fontWeight: 'bold',
                color: 'rgb(0,0,0)',
                fontSize: 18
              },
              scale: true,
              min: 550
            },
            series: [{
              type: 'line',
              name: 'STEP+VARY',
              data: hypervolumes[0],
              itemStyle: {
                color: '#91cc75'
              },
              lineStyle: {
                color: '#91cc75'
              }
            }, {
              type: 'line',
              name: 'STEP+FIXED',
              data: hypervolumes[1],
              itemStyle: {
                color: '#fac858'
              },
              lineStyle: {
                color: '#fac858'
              }
            }],
            legend: {
              show: true,
              top: '80%',
              left: '50%',
              backgroundColor: 'rgba(217,217,217,0.5)',
              borderColor: 'rgb(173,173,173)',
              borderWidth: 1
            },
            toolbox: root.toolbox,
            tooltip: {
              show: true,
              trigger: 'item',
              axisPointer: {
                type: 'cross',
                textStyle: {
                  color: '#fff'
                }
              },
              formatter: '{a}<br/>{c}'
            }
          }
        })
    },
    getParetoFronts () {
      const root = this
      axios.all([
        axios.get('http://localhost:8081/scenariosExtAll?gen=100')
        // axios.get('http://localhost:8081/benchmarkScenario')
      ]).then(responseArr => {// both completed
        root.paretoFrontsOrigin = responseArr[0].data
        const paretoFronts = []
        const pathsGroups = []
        // extract pareto fronts
        for (const scenarios of responseArr[0].data) {
          const front = []
          const pathsGroup = []
          for (const sce of scenarios) {
            // 注意添加的顺序，前两位不能变，后面自定义
            const temp = [sce.economy, sce.environment, sce.scenarioID]
            front.push(temp)
            // 处理数组数据
            // scenario--->path
            const path = []
            for (let i = 0; i < sce.reductionRates.length; i++) {
              const x1 = sce.netcostCumulative[i]
              const y1 = sce.reductionRates[i]
              const z1 = i + 1
              const extra = sce.scenarioID
              const pathPointBase = [x1, y1, z1, extra]
              path.push(pathPointBase)
              // // 最后一个点不添加
              // if (i + 1 > sce.reductionRates.length - 1) {
              //   const pathPointBase = [x1, y1, z1, extra]
              //   path.push(pathPointBase)
              // } else {
              //   const x2 = sce.netcostCumulative[i + 1]
              //   const y2 = sce.reductionRates[i + 1]
              //   const pathPointBase = [x1, y1, z1, extra]
              //   // 当前点的基础之上添加两个点
              //   const pathPointAdd1 = [x1, y2, z1, extra]
              //   const pathPointAdd2 = [x2, y2, z1, extra]
              //   path.push(pathPointBase, pathPointAdd1, pathPointAdd2)
              // }
            }
            // front ---> pathsGroup
            pathsGroup.push(path)
          }
          paretoFronts.push(front)
          // fronts ---> pathsGroups
          pathsGroups.push(pathsGroup)
        }

        root.paretoOptionAll = {
          // title: {
          //   text: '帕累托前沿对比',
          //   left: 'center',
          //   padding: 20
          // },
          grid: {
            top: '5%'
          },
          xAxis: {
            axisLine: {
              lineStyle: {
                color: 'rgb(0,0,0)'
              }
            },
            axisLabel: {
              fontSize: 14
            },
            name: 'Net present value at the first year (CNY 10,000)',
            nameLocation: 'center',
            nameGap: 30,
            nameTextStyle: {
              fontWeight: 'bold',
              color: 'rgb(0,0,0)',
              fontSize: 18
            },
            scale: true,
            // min: 125.0,//全部显示的范围
            // max: 165.0,
            min: 150.0,//部分显示的范围
            max: 156.0
          },
          yAxis: {
            axisLine: {
              lineStyle: {
                color: 'rgb(0,0,0)'
              }
            },
            axisLabel: {
              fontSize: 14
            },
            name: 'Reduction rate of soil erosion (%)',
            nameLocation: 'center',
            nameGap: 35,
            nameTextStyle: {
              fontWeight: 'bold',
              color: 'rgb(0,0,0)',
              fontSize: 18
            },
            scale: true,
            // min: 3.5,//部分显示的范围
            // max: 7
          },
          series: [
            {
              name: root.seriesNames[0],
              symbolSize: 10,
              type: 'scatter',
              data: paretoFronts[0],
              itemStyle: {
                color: '#5470c6'
              }
            },
            {
              name: root.seriesNames[1],
              symbolSize: 10,
              type: 'scatter',
              data: paretoFronts[1],
              itemStyle: {
                color: '#91cc75'
              }
            },
            {
              name: root.seriesNames[2],
              symbolSize: 10,
              type: 'scatter',
              data: paretoFronts[2],
              itemStyle: {
                color: '#fac858'
              }
            },
            {
              name: root.seriesNames[3],
              symbolSize: 10,
              type: 'scatter',
              data: paretoFronts[3],
              itemStyle: {
                color: '#ee6666'
              }
            },
            //   {
            //   name: root.seriesNames[4],
            //   symbolSize: 10,
            //   type: 'scatter',
            //   data: paretoFronts[4],
            //     itemStyle:{
            //       color:'#73c0de'
            //     }
            // }
          ],
          legend: {
            show: true,
            width: '100px',
            // top: '40%',//右中
            // left: '70%',
            top: '70%',//右下
            left: '70%',
            backgroundColor: 'rgba(217,217,217,0.5)',
            borderColor: 'rgb(173,173,173)',
            borderWidth: 1,
            textStyle: {
              fontSize: 14
            }
          },
          toolbox: root.toolbox,
          tooltip: {
            show: true,
            trigger: 'item',
            axisPointer: {
              type: 'cross',
              textStyle: {
                color: '#fff'
              }
            },
            formatter: '{a}<br/>{c}'
          }
        }

        root.path3DOption = {
          title: {
            text: '三类BMP情景实施路径对比',
            left: 'center',
            padding: 20
          },
          xAxis3D: {
            name: 'Net present value (CN￥10000)',
            nameLocation: 'center',
            type: 'value',
            // min: 25
          },
          yAxis3D: {
            name: 'Reduction Rates (%)',
            nameLocation: 'center',
            type: 'value',
            //min: 6
          },
          zAxis3D: {
            name: 'Years of project \n implemented',
            nameLocation: 'center',
            type: 'value',
            minInterval: 1,
            min: 1,
            max: 5
          },
          grid3D: {
            viewControl: {
              projection: 'orthographic'
            }
          },
          legend: {
            show: true,
            top: '10%',
            left: '10%',
            width: '10%',
            backgroundColor: 'rgba(231,231,231,0.5)',
            borderColor: 'rgb(173,173,173)',
            borderWidth: 1
          },
          toolbox: root.toolbox,
          tooltip: {
            show: true,
            trigger: 'item',
            axisPointer: {
              type: 'cross',
              textStyle: {
                color: '#fff'
              }
            },
            formatter: '{a}<br/>{c}'
          },
          series: []
        }

        // console.log(pathsGroups)
        // 每一个group一种颜色
        for (let i = 0; i < pathsGroups.length; i++) {
          const pathsGroup = pathsGroups[i]
          for (let j = 0; j < pathsGroup.length; j++) {
            const path = pathsGroup[j]
            root.path3DOption.series.push({
              type: 'line3D',
              name: root.seriesNames[i],
              data: path,
              lineStyle: root.normalLineStyle
            })
            root.path3DOption.series.push({
              type: 'scatter3D',
              name: root.seriesNames[i],
              data: path,
              symbolSize: 8,
              itemStyle: {
                opacity: 0.4,
                borderWidth: 1,
                borderColor: 'rgba(255,255,255,1)'
              }
            })
          }
        }
      })
    },
    getUnitToGeneIndx () {
      const root = this
      axios.get('http://localhost:8081/unit_to_gene.json').then(function (response) {
        root.unit_to_gene_indx = response.data
      })
    },
    buildStyleSeqCollection () {
      let colorSeqs = [
        ['rgba(189,189,189,0.8)'],
        ['rgb(188,189,220)', 'rgb(158,154,200)', 'rgb(128,125,186)', 'rgb(106,81,163)', 'rgb(74,20,134)'],
        ['rgb(199,233,192)', 'rgb(161,217,155)', 'rgb(116,196,118)', 'rgb(49,163,84)', 'rgb(0,109,44)'],
        ['rgb(254,217,118)', 'rgb(254,178,76)', 'rgb(254,141,60)', 'rgb(240,59,32)', 'rgb(108,0,38)'],
        ['rgb(198,219,239)', 'rgb(158,202,225)', 'rgb(107,174,214)', 'rgb(49,130,189)', 'rgb(8,81,156)']
      ]
      for (let cs of colorSeqs) {
        let styleSeq = []
        for (let color of cs) {
          let temp = new Style({ // default style
            fill: new Fill({
              color: color
            })
          })
          styleSeq.push(temp)
        }
        this.styleSeqCollection.push(styleSeq)
      }
    },
    prepareMap () {
      let mapcontainer = this.$refs.rootmap
      let baseMap = new TileLayer({
        preload: Infinity,
        source: new BingMaps({
          key: 'AigZt6vqR3W3tJYQu_B2MV3Edi1OTYVF0E_bNg3HTNayCFoZzmDdBCDzmDOZP2WI',
          imagerySet: 'AerialWithLabelsOnDemand'
        })
      })
      // baseMap.setVisible(false)
      let spUnitGeoJsonSource = new VectorSource({
        format: new GeoJSON(),
        url: 'http://localhost:8081/slppos_units.geojson'
      })
      this.spUnitVectorLayer = new VectorLayer({
        source: spUnitGeoJsonSource,
        style: new Style({ // default style
          fill: new Fill({
            color: 'rgba(200,0,0,0.5)'
          }),
          // stroke: new Stroke({
          //   color: 'rgba(255,255,255,0.5)'
          // })
        })
      })
      let changtingCenter = olProj.transform([116.46, 25.68], 'EPSG:4326', 'EPSG:3857')
      this.map = new Map({
        target: mapcontainer,
        layers: [
          baseMap,
          this.spUnitVectorLayer
        ],
        view: new View({
          center: changtingCenter,
          zoom: 14
        })
      })

      // let scaleLineControl = new ol_control_CanvasScaleLine({
      //   style:new Style({
      //     text:new Text({
      //       font:"40px Times New Roman"
      //     })
      //   })
      // })
      // this.map.addControl(scaleLineControl)

      //自带的比例尺出图时打印不了
      let scaleline = new ScaleLine()
      this.map.addControl(scaleline)

      let printControl = new ol_control_Print({ quality:1.0 })
      this.map.addControl(printControl)

      /* On print > save image file */
      printControl.on('print', function (e) {
        // console.log(e)
        // Print success
        if (e.canvas) {
          e.canvas.toBlob(function (blob) {
            saveAs(blob, 'map.' + e.imageType.replace('image/', ''))
          }, e.imageType)
        } else {
          console.warn('No canvas to export')
        }
      })
    }
  }
}
</script>

<style scoped>
.chart {
  width: 600px;
  height: 600px;
}

.chartSmall {
  width: 450px;
  height: 450px;
}

.chartBig {
  width: 900px;
  height: 900px;
}

#map {
  height: 650px;
  border: 1px solid #000;
}
</style>

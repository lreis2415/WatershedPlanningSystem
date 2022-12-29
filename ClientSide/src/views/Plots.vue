<template>
  <div>
    <Row>
      <Col span="12">
        <v-chart class="chart" :option="paretoOptionAll" @click="paretoChartClicked"/>
      </Col>
      <Col span="12">
        <v-chart class="chart" :option="hypervolumeOption"/>
      </Col>
    </Row>
    <br>
    <Row>
      <Col span="16" offset="4">
        <v-chart ref="path3DChart" class="chartBig" :option="path3DOption" @click="path3DClicked"/>
      </Col>
    </Row>
<!--    <Row>-->
<!--      <Col span="8">-->
<!--        <v-chart class="chartSmall" :option="paretoOptionType1"/>-->
<!--      </Col>-->
<!--      <Col span="8">-->
<!--        <v-chart class="chartSmall" :option="paretoOptionType2"/>-->
<!--      </Col>-->
<!--      <Col span="8">-->
<!--        <v-chart class="chartSmall" :option="paretoOptionType3"/>-->
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
  LegendComponent
} from 'echarts/components'

import { Line3DChart } from 'echarts-gl/charts'
import { Grid3DComponent } from 'echarts-gl/components'

import axios from 'axios'

echarts.use([
  CanvasRenderer,
  ScatterChart,
  LineChart,
  TitleComponent,
  TooltipComponent,
  ToolboxComponent,
  LegendComponent,
  Line3DChart,
  Grid3DComponent
])
export default {
  name: 'Plots',
  data () {
    return {
      paretoOptionAll: {},
      paretoFrontsDataAll: [],
      hypervolumeOption: {},
      path3DOption: {},
      path3DData: [],
      seriesNames: ['效益变化+一次性资金投入', '效益变化+分阶段资金投入', '效益固定+分阶段资金投入'],
      redColor:'#FF3B1D',
      xAxis: {
        axisLine: {
          lineStyle: {
            color: 'rgb(0,0,0)'
          }
        },
        name: 'Net cost (CN￥10000)',
        nameLocation: 'center',
        nameGap: 30,
        nameTextStyle: {
          fontWeight: 'bold',
          color: 'rgb(0,0,0)'
        },
        scale: true
        // min: 120.0
      },
      yAxis: {
        axisLine: {
          lineStyle: {
            color: 'rgb(0,0,0)'
          }
        },
        name: 'Reduction rate of soil erosion',
        nameLocation: 'center',
        nameGap: 35,
        nameTextStyle: {
          fontWeight: 'bold',
          color: 'rgb(0,0,0)'
        }
      },
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
        opacity: 0.2,
        width: 1
      },
      highlightLineStyle: {
        opacity: 1,
        width: 5
      }
    }
  },
  mounted () {
    // console.log(this.$refs.path3DChart)
    this.getParetoFronts()
    this.getHypervolum()
  },
  methods: {
    paretoChartClicked (params) {
      console.log(params)
    },
    clearHighlightSeries () {
      this.highlightSeries.lineStyle = this.normalLineStyle
      this.$refs.path3DChart.setOption(this.path3DOption)
      this.highlightSeries = null
    },
    path3DClicked (params) {
      if (this.highlightSeries != null) {
        this.clearHighlightSeries()
      }
      console.log(this.path3DOption.series[params.seriesIndex])
      this.highlightSeries = this.path3DOption.series[params.seriesIndex]
      this.highlightSeries.lineStyle = this.highlightLineStyle
      this.$refs.path3DChart.setOption(this.path3DOption)
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
            title: {
              text: '三次优化的Hypervolume',
              left: 'center'
            },
            xAxis: { type: 'value' },
            yAxis: { type: 'value' },
            series: [{
              type: 'line',
              name: root.seriesNames[0],
              data: hypervolumes[0],
            }, {
              type: 'line',
              name: root.seriesNames[1],
              data: hypervolumes[1]
            }, {
              type: 'line',
              name: root.seriesNames[2],
              data: hypervolumes[2]
            }],
            legend: {
              left: 'center',
              top: 'bottom'
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
        axios.get('http://localhost:8081/scenariosExtAll?gen=50&base=1'),
        // axios.get('http://localhost:8081/benchmarkScenario')
      ]).then(responseArr => {
        // both completed
        // console.log(responseArr[1].data)
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
            for (let i = 0; i < sce.sedCumulative.length; i++) {
              const x1 = sce.netcostCumulative[i]
              const y1 = sce.sedCumulative[i] / 1000000
              const z1 = i + 1
              const extra = sce.scenarioID
              // 最后一个点不添加
              if (i + 1 > sce.sedCumulative.length - 1) {
                const pathPointBase = [x1, y1, z1, extra]
                path.push(pathPointBase)
              } else {
                const x2 = sce.netcostCumulative[i + 1]
                const y2 = sce.sedCumulative[i + 1] / 1000000
                const pathPointBase = [x1, y1, z1, extra]
                // 当前点的基础之上添加两个点
                const pathPointAdd1 = [x1, y2, z1, extra]
                const pathPointAdd2 = [x2, y2, z1, extra]
                path.push(pathPointBase, pathPointAdd1, pathPointAdd2)
              }
            }
            // front ---> pathsGroup
            pathsGroup.push(path)
          }
          paretoFronts.push(front)
          // fronts ---> pathsGroups
          pathsGroups.push(pathsGroup)
        }
        root.paretoFrontsDataAll = paretoFronts
        root.path3DData = pathsGroups

        root.paretoOptionAll = {
          title: {
            text: '三种帕累托前沿对比',
            left: 'center'
          },
          xAxis: root.xAxis,
          yAxis: root.yAxis,
          series: [{
            name: root.seriesNames[0],
            symbolSize: 10,
            type: 'scatter',
            data: paretoFronts[0]
          }, {
            name: root.seriesNames[1],
            symbolSize: 10,
            type: 'scatter',
            data: paretoFronts[1]
          }, {
            name: root.seriesNames[2],
            symbolSize: 10,
            type: 'scatter',
            data: paretoFronts[2]
          }],
          legend: {
            left: 'center',
            top: 'bottom'
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
            left: 'center'
          },
          xAxis3D: {
            name: 'Net cost (CN￥10000)',
            nameLocation: 'center',
            type: 'value',
            min: 25
          },
          yAxis3D: {
            name: 'Cumulative sediment (10^6 kg)',
            nameLocation: 'center',
            type: 'value',
            min: 6
          },
          zAxis3D: {
            name: 'Years of project \n implemented',
            nameLocation: 'center',
            type: 'value',
            minInterval: 1,
            min: 1,
            max: 3
          },
          grid3D: {
            viewControl: {
              projection: 'orthographic'
            }
          },
          legend: {
            left: 'center',
            top: 'bottom'
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

        console.log(pathsGroups)
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
          }
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
</style>

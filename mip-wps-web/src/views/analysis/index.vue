<template>
  <Layout>
    <Header>
      <BMPHeader></BMPHeader>
    </Header>
    <Content>
      <div class="bmp-content">
        <h2>{{ $t("message.titleAnalyseResult") }} <Button type="primary" @click="goPrev">{{
            $t("message.backToParamterSetting")
        }}</Button></h2>
        <div class="bmp-table">
          <h3 class="bmp-table__tit">{{ $t("message.optRecords") }}</h3>
          <Tabs type="card">
            <TabPane label="Offline">
              <Table :columns="columns" :data="dataHistory" border class="mb10"
                @on-selection-change="handleHistorySelect">
                <template slot-scope="{ row }" slot="bmpEffectType">
                  {{
                      row.bmpEffectType == 0 ? $t("message.bmpVaryEffect") : $t("message.bmpFixedEffect")
                  }}
                </template>
                <template slot-scope="{ row }" slot="investType">
                  {{ row.investType == 1 ? $t("message.tabMultistageInvestCap") : "" }}
                </template>
              </Table>
              <Button type="primary" class="mr10" @click="compareHistoricalOptimizations('offline')">{{
                  $t("message.compareRecords")
              }}</Button>
            </TabPane>
            <TabPane label="Online">
              <Table :columns="columns" :data="dataHistory30" border class="mb10"
                @on-selection-change="handleHistorySelect30">
                <template slot-scope="{ row }" slot="bmpEffectType">
                  {{
                      row.bmpEffectType == 0 ? $t("message.bmpVaryEffect") : $t("message.bmpFixedEffect")
                  }}
                </template>
                <template slot-scope="{ row }" slot="investType">
                  {{ row.investType == 1 ? $t("message.tabMultistageInvestCap") : "" }}
                </template>
              </Table>
              <Button type="primary" class="mr10" @click="compareHistoricalOptimizations('online')">{{
                  $t("message.visualizeRecords")
              }}</Button>
            </TabPane>
          </Tabs>

          <Divider />

          <!-- <Button type="error">$t("message.deleteRecords")</Button> -->
        </div>

        <div v-if="showPareto">
          <Row :gutter="16" class="mb20">
            <Col span="24">
            <Card>
              <p slot="title"><b>{{ $t("message.paretoFronts") }}</b></p>
              <RadioGroup v-model="focusType" type="button" slot="extra" @on-change="handleFocusTypeChange">
                <Radio label="1">{{ $t("message.showInvestIncome") }}</Radio>
                <Radio label="0">{{ $t("message.showInvest") }}</Radio>
              </RadioGroup>
              <v-chart ref="paretoFrontsChart" class="scatter" :option="paretoFrontsOption" @click="handlePointClick">
              </v-chart>
              <img class="legendParetoFronts" src="@/assets/img/basic/legendParetoFronts.jpg" />
            </Card>
            </Col>
          </Row>
        </div>

          <Row :gutter="16" class="mb20">
            <Col span="12">
              
            <div v-show="showScenarioInfo">
            <Card class="chart-wrap">
              <p slot="title">
                <b>{{ $t("message.selectedRoadmapSpatial") }}</b>
              </p>
              <div style="position: relative">
                <div id="map" ref="rootmap" style="height: 465px"></div>
                <img class="legend" src="@/assets/img/customized/legendStepwise.jpeg" />
              </div>
            </Card></div>
            </Col>
            <Col span="12">
              <div v-if="showScenarioInfo">
            <Card class="chart-wrap">
              <p slot="title">
                <b>{{ $t("message.selectedRoadmapConstructionArea") }}</b>
              </p>
              <Button v-show="curMode == 'offline'" class="mr10" size="small" type="info" slot="extra"
                @click="showCompare = true">{{
                    $t("message.viewAlternatives")
                }}</Button>
              <Button v-show="curMode == 'offline'" size="small" type="success" slot="extra" @click="addCompare">{{
                  $t("message.addAlternatives")
              }}</Button>
              <v-chart :option="areaBarOption" class="bar"></v-chart>
            </Card></div>
            </Col>
          </Row>

          <Row :gutter="16">
            <Col span="12">
              <div v-if="showScenarioInfo">
            <Card class="chart-wrap">
              <p slot="title">
                <b>{{ $t("message.selectedRoadmapEcoPerYear") }}</b>
              </p>
              <v-chart :option="economicBarOption" class="bar"></v-chart>
            </Card></div>
            </Col>
            <Col span="12">
              <div v-if="showScenarioInfo">
            <Card class="chart-wrap">
              <p slot="title">
                <b>{{ $t("message.selectedRoadmapEcoPerYearBMP") }}</b>
              </p>
              <v-chart :option="economicBarOption2" class="bar"></v-chart>
            </Card></div>
            </Col>
          </Row>

          <Row type="flex" justify="center" align="middle">
            <Col span="12"><div v-if="showScenarioInfo">
            <Card class="chart-wrap-3d">
              <p slot="title"><b>{{ $t("message.selectedRoadmapStepwise") }}</b></p>
              <v-chart ref="path3DChart" :option="path3DOption" class="chart-wrap-3d-inner"></v-chart>
            </Card></div>
            </Col>
          </Row>
        

        <Drawer :title="$t('message.altRoadmaps')" :closable="false" v-model="showCompare" width="90">
          <Button type="success" @click="saveCompare" class="mr10">{{ $t("message.saveCompareData") }}</Button>
          <Button type="primary" @click="agreedRoadmap" class="mr10">{{ $t("message.reachConsensus") }}</Button>
          <Button type="error" @click="removeCompare">{{ $t("message.deleteRoadmap") }}</Button>
          <div class="bmp-table">
            <Table :row-class-name="rowClassName" :columns="colsCompare" :data="dataCompare"
              :span-method="handleCompareTableSpan" border stripe height="500" width="100%" class="mb10"
              @on-selection-change="handleCompareTableSelect">
              <template slot-scope="{ row }" slot="name">
                <strong>{{ row.name }}</strong>
              </template>
            </Table>
            <p>{{ $t('message.tableNotes') }}</p>
          </div>
        </Drawer>
      </div>
    </Content>
    <Footer>
      <BMPFooter></BMPFooter>
    </Footer>
  </Layout>
</template>

<script>
import 'ol/ol.css'
import View from 'ol/View'
import Map from 'ol/Map'
import TileLayer from 'ol/layer/Tile'
import OSM from 'ol/source/OSM'
import axios from 'axios'
import common from '@/common'
import BMPHeader from '@/components/header'
import BMPFooter from '@/components/footer'
import BingMaps from 'ol/source/BingMaps'
import VectorSource from 'ol/source/Vector'
import VectorLayer from 'ol/layer/Vector'
import GeoJSON from 'ol/format/GeoJSON'
import { Style, Fill, Stroke, Text } from 'ol/style'
import * as olProj from 'ol/proj'
import { ZoomToExtent, defaults as defaultControls } from 'ol/control'
import { ScaleLine } from 'ol/control'
// ol-ext
import 'ol-ext/dist/ol-ext.css'
import ol_control_Print from 'ol-ext/control/Print'
import ol_control_CanvasScaleLine from 'ol-ext/control/CanvasScaleLine'
import { saveAs } from 'file-saver'
import qs from 'qs'
import { Divider } from 'view-design'

export default {
  name: 'Analysis',
  data() {
    return {
      curMode: "offline",
      consensusIndex: -1,
      reachConsensus: false,
      consensusRoadmap: -1,
      focusType: '1',
      showScenarioInfo: false,
      showPareto: false,
      showCompare: false,
      curScenarioID: '',
      curScenarioObj: null,
      curOptID: '',
      colorsScatter: [
        '#5470c6',
        '#91cc75',
        '#fac858',
        '#ee6666',
        '#37A2DA',
        '#e06343',
        '#37a354',
        '#b55dba',
        '#b5bd48',
        '#8378EA',
        '#96BFFF'
      ],
      columnsInfo: [
        {
          title: ' ',
          align: 'center',
          slot: 'name',
          key: 'index',
          // fixed: 'left',
          width: 100,
          className: 'xxx'
        },
        {
          title: `${this.$t('message.area')}`,
          slot: 'area',
          align: 'center'
        },
        {
          title: this.$t('message.initial'),
          slot: 'capex',
          align: 'center'
        },
        {
          title: this.$t('message.maintain'),
          slot: 'opex',
          align: 'center'
        },
        {
          title: this.$t('message.benefit'),
          align: 'center',
          slot: 'income'
        }
      ],
      scenarioData: [],
      dataHistory: [],
      dataHistory30: [],
      paretoFrontsScatter: [],
      paretoScatterData: [],
      colsCompare: [
        {
          width: 50,
          align: 'center',
          type: 'selection',
          fixed: 'left'
        },
        {
          width: 120,
          align: 'center',
          key: 'name',
          title: 'Roadmap ID',
          fixed: 'left'
        },
        {
          width: 120,
          align: 'center',
          key: 'bmp',
          title: 'BMP',
          fixed: 'left'
        }
      ],
      dataCompare: [],
      map: null,
      selectedHistory: [],
      selectedHistory30: [],
      selectedCompare: [],
      unit_to_gene_indx: null,
      unit_to_gene_indx_30: null,
      spUnitVectorLayer: null,
      spUnitVectorLayer30: null,
      styleSeqCollection: [],
      path3DSeries: [],
      areaBarSeries: [],
      economicBarSeries: [],
      economicBarSeries2: [],
      toolbox: {
        feature: {
          saveAsImage: {
            type: 'jpg',
            pixelRatio: 3,
            show: true
          }
        }
      }
    }
  },
  mounted() {
    this.getHistoricalOptimizations()
    this.getUnitToGeneIndx()
    this.buildStyleSeqCollection()
    this.renderTableCol()
    this.loadCompareData()
  },
  methods: {
    goPrev() {
      location.href = '#/optimize'
    },
    rowClassName(row, index) {
      // console.log(index)
      if (this.consensusIndex !== -1) {
        if (
          index >= this.consensusIndex &&
          index < Number(this.consensusIndex) + 5
        ) {
          return 'demo-table-info-row'
        } else {
          return ''
        }
      } else {
        return ''
      }
    },
    loadCompareData() {
      const userId = localStorage.getItem('bmp_u_id')
      const groupId = localStorage.getItem('bmp_u_groupId')
      axios(`/api/getCompareData?groupId=${groupId}`).then(res => {
        if (res.data && res.data.data) {
          this.dataCompare = res.data.data.dataCompare
          this.consensusIndex = this.dataCompare.findIndex(
            x => x.name == res.data.data.consensusRoadmap
          )
          this.reachConsensus = res.data.data.reachConsensus
          this.consensusRoadmap = res.data.data.consensusRoadmap
        }
      })
    },
    handleCompareTableSelect(data) {
      const tempArr = data.map(item => {
        return item.name
      })
      this.selectedCompare = Array.from(new Set(tempArr))
      // console.log(this.selectedCompare)
    },
    handleFocusTypeChange(selected) {
      for (let i = 0; i < this.paretoScatterData.length; i++) {
        // front
        const curScatter = this.paretoFrontsScatter[i].data
        const curParetoFront = this.paretoScatterData[i]
        for (let j = 0; j < curParetoFront.length; j++) {
          // point
          if (selected == 0) {
            // only focus investment
            curScatter[j].value = curParetoFront[j].listCost
          } else {
            curScatter[j].value = curParetoFront[j].listNetCost
          }
        }
      }
      //
      if (selected == 0) {
        // only focus investment
        this.paretoFrontsOption.xAxis.name =
          'Discounted cost at the first year (unit: CNY 10,000)'
      } else {
        this.paretoFrontsOption.xAxis.name =
          'Net present value at the first year (unit: CNY 10,000)'
      }
    },
    agreedRoadmap() {
      if (this.selectedCompare.length > 1) {
        this.$Message.info(this.$t('message.roadmapNumInfo'))
        return
      }
      this.markPoint(this.selectedCompare[0], 1) // mark type
      this.reachConsensus = true
      this.consensusRoadmap = this.selectedCompare[0]
      // set style
      this.$Message.info(this.$t('message.hasReachedConsensus'))
      // console.log(this.selectedCompare)
      this.markTableColumnData(this.selectedCompare[0])
    },
    markTableColumnData(name) {
      // console.log(this.dataCompare)
      // console.log(name)
      this.consensusIndex = this.dataCompare.findIndex(x => x.name == name)
    },
    saveCompare() {
      const groupId = localStorage.getItem('bmp_u_groupId')
      const jsonObj = {
        groupId: groupId,
        reachConsensus: this.reachConsensus,
        consensusRoadmap: this.consensusRoadmap,
        dataCompare: this.dataCompare
      }
      const headers = {
        // 'content-type': 'application/x-www-form-urlencoded'
        'content-type': 'application/json'
      }
      axios
        .post('/api/saveCompareData', jsonObj, { headers: headers })
        .then(res => {
          // console.log(res.data)
          if (res.data.code === 1) {
            this.$Message.info(this.$t('message.saveCompareDataSuccess'))
          } else {
            this.$Message.info(this.$t('message.saveCompareDataFail'))
          }
        })
    },
    removeCompare() {
      if (!this.selectedCompare.length) {
        this.$Message.info(this.$t('message.selectRoadmaps'));
      } else {
        let i = this.dataCompare.length
        while (i > 0) {
          const item = this.dataCompare[i - 1]
          if (this.selectedCompare.includes(item.name)) {
            this.dataCompare.splice(i - 5, 5)
            this.cancelMarkPoint(item.name)
            if (this.reachConsensus && this.consensusRoadmap == item.name) {
              this.reachConsensus = false
              this.consensusRoadmap = -1
            }
          }
          i -= 5
        }
      }
    },
    handleCompareTableSpan({ row, column, rowIndex, columnIndex }) {
      // merge rows
      if (
        rowIndex % 5 === 0 &&
        (columnIndex === 0 ||
          columnIndex === 1 ||
          columnIndex === 7 ||
          columnIndex === 12 ||
          columnIndex === 17 ||
          columnIndex === 22 ||
          columnIndex === 27 ||
          columnIndex === 28 ||
          columnIndex === 29 ||
          columnIndex === 30 ||
          columnIndex === 31)
      ) {
        return {
          rowspan: 5,
          colspan: 1
        }
      } else if (
        rowIndex % 5 === 1 &&
        (columnIndex === 0 ||
          columnIndex === 1 ||
          columnIndex === 7 ||
          columnIndex === 12 ||
          columnIndex === 17 ||
          columnIndex === 22 ||
          columnIndex === 27 ||
          columnIndex === 28 ||
          columnIndex === 29 ||
          columnIndex === 30 ||
          columnIndex === 31)
      ) {
        return {
          rowspan: 0,
          colspan: 0
        }
      } else if (
        rowIndex % 5 === 2 &&
        (columnIndex === 0 ||
          columnIndex === 1 ||
          columnIndex === 7 ||
          columnIndex === 12 ||
          columnIndex === 17 ||
          columnIndex === 22 ||
          columnIndex === 27 ||
          columnIndex === 28 ||
          columnIndex === 29 ||
          columnIndex === 30 ||
          columnIndex === 31)
      ) {
        return {
          rowspan: 0,
          colspan: 0
        }
      } else if (
        rowIndex % 5 === 3 &&
        (columnIndex === 0 ||
          columnIndex === 1 ||
          columnIndex === 7 ||
          columnIndex === 12 ||
          columnIndex === 17 ||
          columnIndex === 22 ||
          columnIndex === 27 ||
          columnIndex === 28 ||
          columnIndex === 29 ||
          columnIndex === 30 ||
          columnIndex === 31)
      ) {
        return {
          rowspan: 0,
          colspan: 0
        }
      } else if (
        rowIndex % 5 === 4 &&
        (columnIndex === 0 ||
          columnIndex === 1 ||
          columnIndex === 7 ||
          columnIndex === 12 ||
          columnIndex === 17 ||
          columnIndex === 22 ||
          columnIndex === 27 ||
          columnIndex === 28 ||
          columnIndex === 29 ||
          columnIndex === 30 ||
          columnIndex === 31)
      ) {
        return {
          rowspan: 0,
          colspan: 0
        }
      }
    },
    addCompare() {
      // check if it is already in the compare list
      const findedList = this.dataCompare.find(
        item => item.name === this.curScenarioObj.sceid
      )
      if (findedList) {
        this.$Message.info(this.$t('message.roadmapAlreadyInList'))
        return
      }

      this.showCompare = true
      // console.log(this.curScenarioObj)
      const bmps = [
        // { bmpName: this.$t('message.fj'), bmpIndexName: 'FENGJIN' },
        // { bmpName: this.$t('message.stlc'), bmpIndexName: 'SHENGTAILINCAO' },
        // { bmpName: this.$t('message.dxlgz'), bmpIndexName: 'DIXIAOLINGAIZAO' },
        // { bmpName: this.$t('message.jjlg'), bmpIndexName: 'JINGJILINGUO' }
        //use english
        { bmpName: 'CM', bmpIndexName: 'FENGJIN' },
        { bmpName: 'ABHMP', bmpIndexName: 'SHENGTAILINCAO' },
        { bmpName: 'LQFI', bmpIndexName: 'DIXIAOLINGAIZAO' },
        { bmpName: 'EF', bmpIndexName: 'JINGJILINGUO' }
      ]
      let totalArea = 0
      let totalCost = 0
      let totalIncome = 0
      for (var bmpObj of bmps) {
        const obj = {} // data row
        obj.name = this.curScenarioObj.sceid
        obj.bmp = bmpObj.bmpName
        const bmpIndexName = bmpObj.bmpIndexName
        for (let i = 1; i < 6; i++) {
          // year index
          const col = `col${i}1`
          const area = Number(
            this.curScenarioObj.periods[i - 1].BMPS[bmpIndexName].AREA.toFixed(
              4
            )
          )
          obj[col] = area
          totalArea += area

          col = `col${i}2`
          const capex = Number(
            this.curScenarioObj.periods[i - 1].BMPS[bmpIndexName].CAPEX.toFixed(
              4
            )
          )
          obj[col] = capex
          totalCost += capex

          col = `col${i}3`
          const opex = Number(
            this.curScenarioObj.periods[i - 1].BMPS[bmpIndexName].OPEX.toFixed(
              4
            )
          )
          obj[col] = opex
          totalCost += opex

          col = `col${i}4`
          const income = Number(
            this.curScenarioObj.periods[i - 1].BMPS[
              bmpIndexName
            ].INCOME.toFixed(4)
          )
          obj[col] = income
          totalIncome += income

          col = `col${i}5`
          obj[col] = Number(
            this.curScenarioObj.periods[i - 1].SUMMARY.environment.toFixed(4)
          )
        }
        // obj.avgReduction = Number(this.curScenarioObj.environment.toFixed(4))
        // obj.totalArea = Number(totalArea.toFixed(4))
        // obj.totalCost = Number(totalCost.toFixed(4))
        // obj.totalIncome = Number(totalIncome.toFixed(4))
        this.dataCompare.push(obj)
      }
      // summary
      const obj = {} // data row
      obj.name = this.curScenarioObj.sceid
      obj.bmp = this.$t('message.summary')
      for (let i = 1; i < 6; i++) {
        // year index
        const col = `col${i}1`
        const area = Number(
          this.curScenarioObj.periods[i - 1].SUMMARY.AREA.toFixed(4)
        )
        obj[col] = area

        col = `col${i}2`
        const capex = Number(
          this.curScenarioObj.periods[i - 1].SUMMARY.CAPEX.toFixed(4)
        )
        obj[col] = capex

        col = `col${i}3`
        const opex = Number(
          this.curScenarioObj.periods[i - 1].SUMMARY.OPEX.toFixed(4)
        )
        obj[col] = opex

        col = `col${i}4`
        const income = Number(
          this.curScenarioObj.periods[i - 1].SUMMARY.INCOME.toFixed(4)
        )
        obj[col] = income

        col = `col${i}5`
        obj[col] = Number(
          this.curScenarioObj.periods[i - 1].SUMMARY.environment.toFixed(4)
        )
      }

      // obj.avgReduction = Number(this.curScenarioObj.environment.toFixed(4))
      // obj.totalArea = Number(totalArea.toFixed(4))
      // obj.totalCost = Number(totalCost.toFixed(4))
      // obj.totalIncome = Number(totalIncome.toFixed(4))
      this.dataCompare.push(obj)

      // 添加一个红色三角点
      this.markPoint(this.curScenarioObj.sceid, 0)
    },
    renderTableCol() {
      for (let i = 1; i < 6; i++) {
        const yearLab = 'y' + i
        this.colsCompare.push({
          title: this.$t(`message.${yearLab}`),
          align: 'center',
          children: [
            {
              title: this.$t('message.area'),
              key: `col${i}1`,
              width: '120',
              align: 'center'
            },
            {
              title: this.$t('message.initial'),
              key: `col${i}2`,
              width: '130',
              align: 'center'
            },
            {
              title: this.$t('message.maintain'),
              key: `col${i}3`,
              width: '140',
              align: 'center'
            },
            {
              title: this.$t('message.benefit'),
              key: `col${i}4`,
              width: '120',
              align: 'center'
            },
            {
              title: this.$t('message.reductionRate'),
              key: `col${i}5`,
              width: '120',
              align: 'center'
            }
          ]
        })
      }
      // this.colsCompare.push({
      //   title: '年均削减率',
      //   align: 'center',
      //   width: '120',
      //   key: 'avgReduction'
      // })
      // this.colsCompare.push({
      //   title: '总工程面积',
      //   align: 'center',
      //   width: '120',
      //   key: 'totalArea'
      // })
      // this.colsCompare.push({
      //   title: '总支出',
      //   align: 'center',
      //   width: '120',
      //   key: 'totalCost'
      // })
      // this.colsCompare.push({
      //   title: '总收入',
      //   align: 'center',
      //   width: '120',
      //   key: 'totalIncome'
      // })
    },
    getHistoricalOptimizations() {
      const userId = localStorage.getItem('bmp_u_id')
      const groupId = localStorage.getItem('bmp_u_groupId')
      //request offline optimization records
      axios(`/api/historicalOptimizations?groupId=${groupId}&mode=offline`).then(res => {
        if (res.data && res.data.data && res.data.data.length) {
          const formatData = res.data.data.map((item, index) => {
            const investList = item.stepwiseInvestMax.split(',')
            item.year1 = investList[0]
            item.year2 = investList[1]
            item.year3 = investList[2]
            item.year4 = investList[3]
            item.year5 = investList[4]
            return item
          })

          this.dataHistory = formatData
          // console.log(this.dataHistory)
          let baseInvestment = this.dataHistory[0]
          for (let i = 1; i < this.dataHistory.length; i++) {
            let tempInvestment = this.dataHistory[i]
            let cellClassName = {}
            for (let j = 1; j < 6; j++) {
              let key = 'year' + j
              if (tempInvestment[key] > baseInvestment[key]) {
                cellClassName[key] = 'demo-table-info-bigger'
              } else if (tempInvestment[key] < baseInvestment[key]) {
                cellClassName[key] = 'demo-table-info-smaller'
              }
              // else{
              //   cellClassName[key] = 'demo-table-info-equal'
              // }
            }
            tempInvestment['cellClassName'] = cellClassName
            baseInvestment = tempInvestment
          }
        }
      })

      //request online optimization records
      axios(`/api/historicalOptimizations?groupId=${groupId}&mode=online`).then(res => {
        if (res.data && res.data.data && res.data.data.length) {
          const formatData = res.data.data.map((item, index) => {
            const investList = item.stepwiseInvestMax.split(',')
            item.year1 = investList[0]
            item.year2 = investList[1]
            item.year3 = investList[2]
            item.year4 = investList[3]
            item.year5 = investList[4]
            return item
          })

          this.dataHistory30 = formatData
          // console.log(this.dataHistory30)
          let baseInvestment = this.dataHistory30[0]
          for (let i = 1; i < this.dataHistory30.length; i++) {
            let tempInvestment = this.dataHistory30[i]
            let cellClassName = {}
            for (let j = 1; j < 6; j++) {
              let key = 'year' + j
              if (tempInvestment[key] > baseInvestment[key]) {
                cellClassName[key] = 'demo-table-info-bigger'
              } else if (tempInvestment[key] < baseInvestment[key]) {
                cellClassName[key] = 'demo-table-info-smaller'
              }
              // else{
              //   cellClassName[key] = 'demo-table-info-equal'
              // }
            }
            tempInvestment['cellClassName'] = cellClassName
            baseInvestment = tempInvestment
          }
        }
      })
    },
    handleHistorySelect(data) {
      this.selectedHistory = data.map(item => {
        return item.id
      })
      // console.log(this.selectedHistory)
    },
    handleHistorySelect30(data) {
      this.selectedHistory30 = data.map(item => {
        return item.id
      })
      // console.log(this.selectedHistory30)
    },
    compareHistoricalOptimizations(mode) {
      if (mode == 'offline') {
        if (!this.selectedHistory.length) {
          this.$Message.info(this.$t('message.selectCompareRecords'))
          return
        }
      } else {
        if (!this.selectedHistory30.length) {
          this.$Message.info(this.$t('message.selectCompareRecords'))
          return
        }
      }

      if (this.$refs.paretoFrontsChart) {
        // clear
        // console.log(this.$refs.paretoFrontsChart)
        this.paretoFrontsScatter = []
        this.$refs.paretoFrontsChart.setOption(this.paretoFrontsOption, true)
      }

      const userId = localStorage.getItem('bmp_u_id')
      const groupId = localStorage.getItem('bmp_u_groupId')
      if (mode == 'offline') {
        const ids = this.selectedHistory.join()
        axios
          .get(`/api/getParetoFront?groupId=${groupId}&optIds=${ids}&mode=offline`)
          .then(res => {
            this.showPareto = true
            this.drawParetoFronts(res.data.data)
            this.curMode = "offline"
            this.showScenarioInfo = false

            for (let i = 0; i < this.dataCompare.length; i++) {
              const tempRoadmap = this.dataCompare[i]
              if (
                tempRoadmap.name == this.consensusRoadmap &&
                this.reachConsensus
              ) {
                this.markPoint(tempRoadmap.name, 1)
                // set style
              } else {
                this.markPoint(tempRoadmap.name, 0)
              }
            }
          })
      } else {
        const ids = this.selectedHistory30.join()
        axios
          .get(`/api/getParetoFront?groupId=${groupId}&optIds=${ids}&mode=online`)
          .then(res => {
            this.showPareto = true
            this.drawParetoFronts(res.data.data)
            this.curMode = "online"
            this.showScenarioInfo = false
          })
      }
    },
    drawParetoFronts(data) {
      console.log(data)
      this.paretoScatterData = data.map(item => {
        const name = item.optName + ' By ' + item.stakeholderName + ' (' + item.stakeholderRole+ ')'
        return item.scenarios.map(item1 => {
          return {
            name: name,
            scenarioID: item1.scenarioID,
            optId: item.optId,
            listNetCost: [item1.economy, item1.environment],
            listCost: [item1.economyCost, item1.environment]
          }
        })
      })
      this.paretoFrontsScatter = this.paretoScatterData.map((item, index) => {
        return {
          name: item[0].name,
          type: 'scatter',
          emphasis: {
            itemStyle: {
              color: 'red',
              borderColor: '#fff'
            }
          },
          // color: this.colorsScatter[index],
          select: {
            itemStyle: {
              color: 'red',
              borderColor: '#fff'
            }
          },
          selectedMode: false,
          data: item.map((item1, index1) => {
            return {
              value: this.focusType == 1 ? item1.listNetCost : item1.listCost,
              label: {},
              itemStyle: {
                color: this.colorsScatter[index]
              },
              symbolSize: 12,
              symbol: 'circle',
              scenarioID: item1.scenarioID,
              optName: item[0].name,
              optId: item[0].optId
            }
          })
        }
      })
    },
    handlePointClick(params) {
      if (params.componentType === 'series') {
        this.curScenarioID = params.data.scenarioID
        this.curOptID = params.data.optId
        this.selectCurrentPoint()
        this.getScenarioInfo()
      }
    },
    selectCurrentPoint() {
      this.paretoFrontsScatter.map(item => {
        item.data.map(item1 => {
          if (item1.scenarioID === this.curScenarioID) {
            item1.itemStyle.color = 'black'
          } else if (
            item1.symbol !== 'triangle' &&
            item1.symbol !== 'diamond'
          ) {
            item1.itemStyle.color = item.color
          }
        })
      })
    },
    markPoint(id, type) {
      this.paretoFrontsScatter.map(item => {
        item.data.map(item1 => {
          if (item1.scenarioID == id) {
            item1.itemStyle.color = 'black'
            if (type == 0) {
              item1.symbol = 'triangle'
              item1.symbolSize = 16
            } else {
              item1.symbol = 'diamond'
              item1.symbolSize = 20
            }
          }
        })
      })
    },
    cancelMarkPoint(id) {
      this.paretoFrontsScatter.map(item => {
        item.data.map(item1 => {
          if (item1.scenarioID == id) {
            item1.itemStyle.color = 'black'
            item1.symbol = 'circle'
            item1.symbolSize = 12
          }
        })
      })
    },
    updateScenarioMap(scenario) {
      const root = this
      if (root.curMode == 'offline') {
        this.spUnitVectorLayer.setStyle(function (feature, res) {
          const slpposUnit = feature.values_.DN
          const geneIndex = root.unit_to_gene_indx[slpposUnit] // slppos unit id => gene index
          const geneValue = scenario.gene_values[geneIndex]
          // console.log(slpposUnit, gene_indx, geneValue)
          if (geneValue === 0) {
            return root.styleSeqCollection[0][0]
          } else {
            const bmpType = Math.floor(geneValue / 1000)
            const implYear = geneValue % 1000
            return root.styleSeqCollection[bmpType][5 - implYear]
          }
        })
        this.spUnitVectorLayer30.setVisible(false)
        this.spUnitVectorLayer.setVisible(true)
      } else {
        this.spUnitVectorLayer30.setStyle(function (feature, res) {
          const slpposUnit = feature.values_.DN
          const geneIndex = root.unit_to_gene_indx_30[slpposUnit] // slppos unit id => gene index
          const geneValue = scenario.gene_values[geneIndex]
          // console.log(slpposUnit, gene_indx, geneValue)
          if (geneValue === 0) {
            return root.styleSeqCollection[0][0]
          } else {
            const bmpType = Math.floor(geneValue / 1000)
            const implYear = geneValue % 1000
            return root.styleSeqCollection[bmpType][5 - implYear]
          }
        })
        this.spUnitVectorLayer.setVisible(false)
        this.spUnitVectorLayer30.setVisible(true)
      }
    },
    updateAreaBarChart(scenario) {
      // console.log(scenario)
      this.areaBarSeries = []
      const fjAreas = []
      const stlcAreas = []
      const dxlgzAreas = []
      const jjlgAreas = []
      for (let i = 0; i < 5; i++) {
        fjAreas.push(scenario.periods[i].BMPS.FENGJIN.AREA)
        stlcAreas.push(scenario.periods[i].BMPS.SHENGTAILINCAO.AREA)
        dxlgzAreas.push(scenario.periods[i].BMPS.DIXIAOLINGAIZAO.AREA)
        jjlgAreas.push(scenario.periods[i].BMPS.JINGJILINGUO.AREA)
      }
      this.areaBarSeries.push({
        name: this.$t('message.fj'),
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#6a51a4'
        },
        data: fjAreas.map(i => Math.cbrt(i))
      })
      this.areaBarSeries.push({
        name: this.$t('message.stlc'),
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#30a355'
        },
        data: stlcAreas.map(i => Math.cbrt(i))
      })
      this.areaBarSeries.push({
        name: this.$t('message.dxlgz'),
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#f03b20'
        },
        data: dxlgzAreas.map(i => Math.cbrt(i))
      })
      this.areaBarSeries.push({
        name: this.$t('message.jjlg'),
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#3282bd'
        },
        data: jjlgAreas.map(i => Math.cbrt(i))
      })
    },
    updateEconomicBarChart(scenario) {
      this.economicBarSeries = []
      const capex = []
      const opex = []
      const income = []
      const environments = []
      for (let i = 0; i < 5; i++) {
        capex.push(scenario.periods[i].SUMMARY.CAPEX)
        opex.push(scenario.periods[i].SUMMARY.OPEX)
        income.push(-scenario.periods[i].SUMMARY.INCOME)
        environments.push(scenario.periods[i].SUMMARY.environment)
      }
      this.economicBarSeries.push({
        name: this.$t('message.initial'),
        stack: 'Total',
        type: 'bar',
        data: capex
      })
      this.economicBarSeries.push({
        name: this.$t('message.maintain'),
        stack: 'Total',
        type: 'bar',
        data: opex
      })
      this.economicBarSeries.push({
        name: this.$t('message.benefit'),
        stack: 'Total',
        type: 'bar',
        data: income
      })
      this.economicBarSeries.push({
        name: this.$t('message.accumReductionRate'),
        type: 'line',
        yAxisIndex: 1,
        data: environments
      })
      ///
      this.economicBarSeries2 = []
      const fjCapex = []
      const fjOpex = []
      const fjOpexDisplay = []
      const fjIncome = []
      const stlcCapex = []
      const stlcOpex = []
      const stlcOpexDisplay = []
      const stlcIncome = []
      const dxlgzCapex = []
      const dxlgzOpex = []
      const dxlgzOpexDisplay = []
      const dxlgzIncome = []
      const jjlgCapex = []
      const jjlgOpex = []
      const jjlgOpexDisplay = []
      const jjlgIncome = []
      for (let i = 0; i < 5; i++) {
        fjCapex.push(scenario.periods[i].BMPS.FENGJIN.CAPEX)
        fjOpex.push(scenario.periods[i].BMPS.FENGJIN.OPEX)
        fjOpexDisplay.push({
          value: Math.cbrt(fjCapex[i] + fjOpex[i]) - Math.cbrt(fjCapex[i]),
          label: fjOpex[i]
        })
        fjIncome.push(-scenario.periods[i].BMPS.FENGJIN.INCOME)

        stlcCapex.push(scenario.periods[i].BMPS.SHENGTAILINCAO.CAPEX)
        stlcOpex.push(scenario.periods[i].BMPS.SHENGTAILINCAO.OPEX)
        stlcOpexDisplay.push({
          value:
            Math.cbrt(stlcCapex[i] + stlcOpex[i]) - Math.cbrt(stlcCapex[i]),
          label: stlcOpex[i]
        })
        stlcIncome.push(-scenario.periods[i].BMPS.SHENGTAILINCAO.INCOME)

        dxlgzCapex.push(scenario.periods[i].BMPS.DIXIAOLINGAIZAO.CAPEX)
        dxlgzOpex.push(scenario.periods[i].BMPS.DIXIAOLINGAIZAO.OPEX)
        dxlgzOpexDisplay.push({
          value:
            Math.cbrt(dxlgzCapex[i] + dxlgzOpex[i]) - Math.cbrt(dxlgzCapex[i]),
          label: dxlgzOpex[i]
        })
        dxlgzIncome.push(-scenario.periods[i].BMPS.DIXIAOLINGAIZAO.INCOME)

        jjlgCapex.push(scenario.periods[i].BMPS.JINGJILINGUO.CAPEX)
        jjlgOpex.push(scenario.periods[i].BMPS.JINGJILINGUO.OPEX)
        jjlgOpexDisplay.push({
          value:
            Math.cbrt(jjlgCapex[i] + jjlgOpex[i]) - Math.cbrt(jjlgCapex[i]),
          label: jjlgOpex[i]
        })
        jjlgIncome.push(-scenario.periods[i].BMPS.JINGJILINGUO.INCOME)
      }
      this.economicBarSeries2.push({
        name: this.$t('message.fj') + '-' + this.$t('message.initial'),
        stack: 'fj',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#7126A7'
        },
        data: fjCapex.map(i => Math.cbrt(i))
      })
      this.economicBarSeries2.push({
        name: this.$t('message.fj') + '-' + this.$t('message.maintain'),
        stack: 'fj',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#8843B9'
        },
        data: fjOpexDisplay
      })
      this.economicBarSeries2.push({
        name: this.$t('message.fj') + '-' + this.$t('message.benefit'),
        stack: 'fj',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#A165CD'
        },
        data: fjIncome.map(i => Math.cbrt(i))
      })
      this.economicBarSeries2.push({
        name: this.$t('message.stlc') + '-' + this.$t('message.initial'),
        stack: 'stlc',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#00C53C'
        },
        data: stlcCapex.map(i => Math.cbrt(i))
      })
      this.economicBarSeries2.push({
        name: this.$t('message.stlc') + '-' + this.$t('message.maintain'),
        stack: 'stlc',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#3ADC6B'
        },
        data: stlcOpexDisplay
      })
      this.economicBarSeries2.push({
        name: this.$t('message.stlc') + '-' + this.$t('message.benefit'),
        stack: 'stlc',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#64E48B'
        },
        data: stlcIncome.map(i => Math.cbrt(i))
      })
      this.economicBarSeries2.push({
        name: this.$t('message.dxlgz') + '-' + this.$t('message.initial'),
        stack: 'dxlgz',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#ED244F'
        },
        data: dxlgzCapex.map(i => Math.cbrt(i))
      })
      this.economicBarSeries2.push({
        name: this.$t('message.dxlgz') + '-' + this.$t('message.maintain'),
        stack: 'dxlgz',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#F1476C'
        },
        data: dxlgzOpexDisplay
      })
      this.economicBarSeries2.push({
        name: this.$t('message.dxlgz') + '-' + this.$t('message.benefit'),
        stack: 'dxlgz',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#F56B89'
        },
        data: dxlgzIncome.map(i => Math.cbrt(i))
      })
      this.economicBarSeries2.push({
        name: this.$t('message.jjlg') + '-' + this.$t('message.initial'),
        stack: 'jjlg',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#106BB1'
        },
        data: jjlgCapex.map(i => Math.cbrt(i))
      })
      this.economicBarSeries2.push({
        name: this.$t('message.jjlg') + '-' + this.$t('message.maintain'),
        stack: 'jjlg',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#3281BD'
        },
        data: jjlgOpexDisplay
      })
      this.economicBarSeries2.push({
        name: this.$t('message.jjlg') + '-' + this.$t('message.benefit'),
        stack: 'jjlg',
        type: 'bar',
        barGap: 0,
        itemStyle: {
          color: '#5297CC'
        },
        data: jjlgIncome.map(i => Math.cbrt(i))
      })
      // this.economicBarSeries2.push({
      //   name: this.$t('message.accumReductionRate'),
      //   type: 'line',
      //   yAxisIndex: 1,
      //   data: environments
      // })
    },
    updatePathChart(scenario) {
      // scenario--->path
      const path = []
      const prjPoints = []
      const perpendicularLines = []
      for (let i = 0; i < scenario.redunction_rates.length; i++) {
        const x1 = scenario.net_cost_cumul[i]
        const y1 = scenario.redunction_rates[i]
        const z1 = i + 1
        const pathPointBase = [x1, y1, z1]
        path.push(pathPointBase)
        const prjPoint = [x1, y1, 1]
        prjPoints.push(prjPoint)
        perpendicularLines.push([pathPointBase, prjPoint])
      }
      // path
      this.path3DSeries = []
      this.path3DSeries.push({
        animation: false,
        type: 'line3D',
        name: scenario.sceid,
        data: path,
        lineStyle: {
          opacity: 1,
          width: 3
        }
      })
      // point
      this.path3DSeries.push({
        animation: false,
        type: 'scatter3D',
        name: scenario.sceid,
        data: path,
        symbolSize: 8,
        itemStyle: {
          opacity: 0.4,
          borderWidth: 1,
          borderColor: 'rgba(255,255,255,1)'
        }
      })
      // project point on z=1
      this.path3DSeries.push({
        animation: false,
        type: 'scatter3D',
        name: scenario.sceid,
        data: prjPoints,
        symbolSize: 8,
        itemStyle: {
          opacity: 0.4,
          borderWidth: 1,
          borderColor: 'rgba(255,255,255,1)'
        }
      })
      // perpendicular line
      // console.log(perpendicularLines)
      for (const line of perpendicularLines) {
        this.path3DSeries.push({
          animation: false,
          type: 'line3D',
          name: scenario.sceid,
          data: line,
          lineStyle: {
            color: 'black',
            opacity: 1,
            width: 1
          }
        })
      }
    },
    getScenarioInfo() {
      const userId = localStorage.getItem('bmp_u_id')
      const groupId = localStorage.getItem('bmp_u_groupId')
      axios
        .get(
          `/api/getScenarioByID?groupId=${groupId}&optId=${this.curOptID}&scenarioId=${this.curScenarioID}&mode=${this.curMode}`
        )
        .then(res => {
          const data = res.data
          console.log(data)
          this.showScenarioInfo = true
          // this.renderInfo(data.periods)
          if (!this.map) {
            this.$nextTick(() => {
              this.initMap()
              this.updateScenarioMap(data)
            })
          } else {
            this.updateScenarioMap(data)
          }
          this.updateAreaBarChart(data)
          this.updateEconomicBarChart(data)
          this.updatePathChart(data)
          this.curScenarioObj = data
        })
    },
    renderInfo(periods) {
      const list = [
        {
          name: this.$t('message.fj')
        },
        {
          name: this.$t('message.stlc')
        },
        {
          name: this.$t('message.dxlgz')
        },
        {
          name: this.$t('message.jjlg')
        },
        {
          name: this.$t('message.summary')
        }
      ]

      list.map((item, index) => {
        item.areaYear = {
          tooltip: {
            formatter: '{c}'
          },
          grid: {
            top: 8,
            bottom: 8,
            left: 4,
            right: 4
          },
          xAxis: {
            type: 'category',
            show: false,
            data: [1, 2, 3, 4, 5],
            axisLabel: {
              fontSize: 14,
              fontFamily: 'Times New Roman'
            },
          },
          yAxis: {
            type: 'value',
            show: false,
            axisLabel: {
              fontSize: 14,
              fontFamily: 'Times New Roman'
            },
          },
          series: [
            {
              data: [],
              type: 'bar',
              showBackground: true,
              color: '#3ba272',
              backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
              }
            }
          ]
        }
        item.capexYear = {
          tooltip: {
            formatter: '{c}'
          },
          grid: {
            top: 8,
            bottom: 8
          },
          xAxis: {
            type: 'category',
            show: false
          },
          yAxis: {
            type: 'value',
            show: false
          },
          series: [
            {
              data: [],
              type: 'bar',
              showBackground: true,
              color: '#fc8452',
              backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
              }
            }
          ]
        }
        item.opexYear = {
          tooltip: {
            formatter: '{c}'
          },
          grid: {
            top: 8,
            bottom: 8
          },
          xAxis: {
            type: 'category',
            show: false
          },
          yAxis: {
            type: 'value',
            show: false
          },
          series: [
            {
              data: [],
              type: 'bar',
              showBackground: true,
              color: '#9a60b4',
              backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
              }
            }
          ]
        }
        item.incomeYear = {
          tooltip: {
            formatter: '{c}'
          },
          grid: {
            top: 8,
            bottom: 8
          },
          xAxis: {
            type: 'category',
            show: false
          },
          yAxis: {
            type: 'value',
            show: false
          },
          series: [
            {
              data: [],
              type: 'bar',
              color: '#ea7ccc',
              showBackground: true,
              backgroundStyle: {
                color: 'rgba(180, 180, 180, 0.2)'
              }
            }
          ]
        }
      })

      for (let i = 0; i < periods.length; i++) {
        list[0].areaYear.series[0].data.push(
          periods[i].BMPS.FENGJIN.AREA.toFixed(2)
        )
        list[0].capexYear.series[0].data.push(
          periods[i].BMPS.FENGJIN.CAPEX.toFixed(2)
        )
        list[0].opexYear.series[0].data.push(
          periods[i].BMPS.FENGJIN.OPEX.toFixed(2)
        )
        list[0].incomeYear.series[0].data.push(
          periods[i].BMPS.FENGJIN.INCOME.toFixed(2)
        )

        list[1].areaYear.series[0].data.push(
          periods[i].BMPS.SHENGTAILINCAO.AREA.toFixed(2)
        )
        list[1].capexYear.series[0].data.push(
          periods[i].BMPS.SHENGTAILINCAO.CAPEX.toFixed(2)
        )
        list[1].opexYear.series[0].data.push(
          periods[i].BMPS.SHENGTAILINCAO.OPEX.toFixed(2)
        )
        list[1].incomeYear.series[0].data.push(
          periods[i].BMPS.SHENGTAILINCAO.INCOME.toFixed(2)
        )

        list[2].areaYear.series[0].data.push(
          periods[i].BMPS.DIXIAOLINGAIZAO.AREA.toFixed(2)
        )
        list[2].capexYear.series[0].data.push(
          periods[i].BMPS.DIXIAOLINGAIZAO.CAPEX.toFixed(2)
        )
        list[2].opexYear.series[0].data.push(
          periods[i].BMPS.DIXIAOLINGAIZAO.OPEX.toFixed(2)
        )
        list[2].incomeYear.series[0].data.push(
          periods[i].BMPS.DIXIAOLINGAIZAO.INCOME.toFixed(2)
        )

        list[3].areaYear.series[0].data.push(
          periods[i].BMPS.JINGJILINGUO.AREA.toFixed(4)
        )
        list[3].capexYear.series[0].data.push(
          periods[i].BMPS.JINGJILINGUO.CAPEX.toFixed(4)
        )
        list[3].opexYear.series[0].data.push(
          periods[i].BMPS.JINGJILINGUO.OPEX.toFixed(4)
        )
        list[3].incomeYear.series[0].data.push(
          periods[i].BMPS.JINGJILINGUO.INCOME.toFixed(4)
        )

        list[4].areaYear.series[0].data.push(periods[i].SUMMARY.AREA.toFixed(2))
        list[4].capexYear.series[0].data.push(
          periods[i].SUMMARY.CAPEX.toFixed(2)
        )
        list[4].opexYear.series[0].data.push(periods[i].SUMMARY.OPEX.toFixed(2))
        list[4].incomeYear.series[0].data.push(
          periods[i].SUMMARY.INCOME.toFixed(2)
        )
      }

      this.scenarioData = list
    },
    initMap() {
      const baseMap = new TileLayer({
        preload: Infinity,
        source: new BingMaps({
          key:
            'AigZt6vqR3W3tJYQu_B2MV3Edi1OTYVF0E_bNg3HTNayCFoZzmDdBCDzmDOZP2WI',
          imagerySet: 'AerialWithLabelsOnDemand'
        })
      })

      const changtingCenter = olProj.transform(
        [116.46, 25.68],
        'EPSG:4326',
        'EPSG:3857'
      )

      const spUnitGeoJsonSource = new VectorSource({
        format: new GeoJSON(),
        url: '/api/slppos_units.geojson'
      })

      this.spUnitVectorLayer = new VectorLayer({
        source: spUnitGeoJsonSource,
        style: new Style({
          fill: new Fill({
            color: 'rgba(200,0,0,0.5)'
          })
          // stroke: new Stroke({
          //   color: 'rgba(255,255,255,0.5)'
          // })
        })
      })

      const spUnitGeoJsonSource30 = new VectorSource({
        format: new GeoJSON(),
        url: '/api/slppos_units_30.geojson'
      })

      this.spUnitVectorLayer30 = new VectorLayer({
        source: spUnitGeoJsonSource30,
        style: new Style({
          fill: new Fill({
            color: 'rgba(200,0,0,0.5)'
          })
          // stroke: new Stroke({
          //   color: 'rgba(255,255,255,0.5)'
          // })
        }),
        visible: false // not visible by default
      })

      this.map = new Map({
        target: this.$refs.rootmap,
        controls: defaultControls().extend([
          new ZoomToExtent({
            label: 'L',
            tipLabel: 'locate to study area',
            extent: [12961004.992139718, 2957279.799988502, 12967530.803429564, 2961722.702257578]
          })
        ]),
        layers: [baseMap, this.spUnitVectorLayer, this.spUnitVectorLayer30],
        view: new View({
          center: changtingCenter,
          zoom: 14
        })
      })
      const scaleLineControl = new ol_control_CanvasScaleLine({
        style: new Style({
          text: new Text({
            font: '20px Times New Roman'
          })
        })
      })
      this.map.addControl(scaleLineControl)
      // The built-in scaleline cannot be printed when download
      // let scaleline = new ScaleLine()
      // this.map.addControl(scaleline)

      const printControl = new ol_control_Print({ quality: 1.0 })
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
    },
    getUnitToGeneIndx() {
      const root = this
      axios.get('/api/customized/unit_to_gene.json').then(function (response) {
        root.unit_to_gene_indx = response.data
      })
      axios.get('/api/customized/unit_to_gene_30.json').then(function (response) {
        root.unit_to_gene_indx_30 = response.data
      })
    },
    buildStyleSeqCollection() {
      const colorSeqs = [
        ['rgba(189,189,189,0.8)'],
        [
          'rgb(188,189,220)',
          'rgb(158,154,200)',
          'rgb(128,125,186)',
          'rgb(106,81,163)',
          'rgb(74,20,134)'
        ],
        [
          'rgb(199,233,192)',
          'rgb(161,217,155)',
          'rgb(116,196,118)',
          'rgb(49,163,84)',
          'rgb(0,109,44)'
        ],
        [
          'rgb(254,217,118)',
          'rgb(254,178,76)',
          'rgb(254,141,60)',
          'rgb(240,59,32)',
          'rgb(108,0,38)'
        ],
        [
          'rgb(198,219,239)',
          'rgb(158,202,225)',
          'rgb(107,174,214)',
          'rgb(49,130,189)',
          'rgb(8,81,156)'
        ]
      ]
      for (const cs of colorSeqs) {
        const styleSeq = []
        for (const color of cs) {
          const temp = new Style({
            // default style
            fill: new Fill({
              color: color
            })
          })
          styleSeq.push(temp)
        }
        this.styleSeqCollection.push(styleSeq)
      }
    }
  },
  components: {
    BMPHeader,
    BMPFooter,
    Divider
  },
  computed: {
    columns() {
      const data = [
        {
          type: 'selection',
          width: 40,
          align: 'left'
        },
        {
          title: this.$t('message.optName'),
          key: 'name'
        },
        {
          title: this.$t('message.investType'),
          slot: 'investType'
        },
        {
          title: this.$t('message.stepInvestLimits'),
          key: 'money',
          children: [
            {
              title: '1',
              key: 'year1',
              align: 'center',
              width: 60
            },
            {
              title: '2',
              key: 'year2',
              align: 'center',
              width: 60
            },
            {
              title: '3',
              key: 'year3',
              align: 'center',
              width: 60
            },
            {
              title: '4',
              key: 'year4',
              align: 'center',
              width: 60
            },
            {
              title: '5',
              key: 'year5',
              align: 'center',
              width: 60
            }
          ]
        },
        {
          title: this.$t('message.discountRate'),
          key: 'discountRate',
          width: 100
        },
        {
          title: this.$t('message.bmpEnvMode'),
          slot: 'bmpEffectType'
        },
        {
          title: this.$t('message.optAlgorithmParameters'),
          key: 'params',
          children: [
            {
              title: this.$t('message.objective1'),
              key: 'objective1'
            },
            {
              title: this.$t('message.objective2'),
              key: 'objective2'
            },
            // {
            //   title: this.$t('message.moAlgorithm'),
            //   key: 'algorithm'
            // },
            // {
            //   title: this.$t('message.genNumbers'),
            //   key: 'generation'
            // },
            // {
            //   title: this.$t('message.popSize'),
            //   key: 'population'
            // },
            // {
            //   title: this.$t('message.crossoverRate'),
            //   key: 'crossoverRate'
            // },
            // {
            //   title: this.$t('message.mutateRate'),
            //   key: 'mutateRate'
            // },
            // {
            //   title: this.$t('message.selectRate'),
            //   key: 'selectRate'
            // }
          ]
        },
        {
          title: this.$t('message.optNotes'),
          key: 'notes',
          tooltip: true,
          width: 120
        }
      ]
      return data;
    },
    paretoFrontsOption() {
      return {
        // title: {
        //   text: 'Pareto fronts',
        //   left: 'center',
        //   padding: 20
        // },
        grid: {
          top: 30
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            textStyle: {
              color: '#fff'
            }
          }
        },
        xAxis: {
          axisLine: {
            lineStyle: {
              color: 'rgb(0,0,0)'
            }
          },
          axisLabel: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
          name: this.$t('message.npv'),
          nameLocation: 'center',
          nameGap: 30,
          nameTextStyle: {
            fontWeight: 'bold',
            color: 'rgb(0,0,0)',
            fontSize: 18,
            fontFamily: 'Times New Roman'
          },
          scale: true
        },
        yAxis: {
          axisLine: {
            lineStyle: {
              color: 'rgb(0,0,0)'
            }
          },
          axisLabel: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
          name: this.$t('message.reductionRateAxisLabel'),
          nameLocation: 'center',
          nameGap: 35,
          nameTextStyle: {
            fontWeight: 'bold',
            color: 'rgb(0,0,0)',
            fontSize: 18,
            fontFamily: 'Times New Roman'
          },
          scale: true
          // min: 3.5,//部分显示的范围
          // max: 7
        },
        legend: {
          show: true,
          right: 20,
          top: 300,
          orient: 'vertical',
          backgroundColor: 'rgba(231,231,231,0.5)',
          borderColor: 'rgb(173,173,173)',
          borderWidth: 1,
          textStyle: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          }
        },
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
        toolbox: this.toolbox,
        series: this.paretoFrontsScatter
      }
    },
    path3DOption() {
      return {
        // title: {
        //   text: 'BMP scenario stepwise effectiveness',
        //   left: 'center'
        // },
        xAxis3D: {
          name: this.$t('message.npvBreak'),
          nameLocation: 'center',
          type: 'value',
          nameGap: 10,
          axisLabel: {
            margin: 5,
            fontFamily: 'Times New Roman'
          }
          // min: 0,
          // max: 200
        },
        yAxis3D: {
          name: this.$t('message.accumReductionRateAxisLabelBreak'),
          nameLocation: 'center',
          type: 'value',
          nameGap: 10,
          axisLabel: {
            margin: 5,
            fontFamily: 'Times New Roman'
          }
          // min: 0,
          // max: 10
        },
        zAxis3D: {
          name: this.$t('message.yearAxisLabel'),
          nameLocation: 'center',
          type: 'value',
          nameGap: 10,
          axisLabel: {
            margin: 5,
            fontFamily: 'Times New Roman'
          },
          minInterval: 1,
          min: 1,
          max: 5
        },
        grid3D: {
          top: 0,
          viewControl: {
            projection: 'orthographic'
          }
        },
        // legend: {
        //   show: true,
        //   top: '10%',
        //   left: '10%',
        //   width: '10%',
        //   backgroundColor: 'rgba(231,231,231,0.5)',
        //   borderColor: 'rgb(173,173,173)',
        //   borderWidth: 1,
        //   textStyle: {
        //       fontSize: 14,
        //       fontFamily: 'Times New Roman'
        //     }
        // },
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
        toolbox: this.toolbox,
        series: this.path3DSeries
      }
    },
    areaBarOption() {
      return {
        xAxis: {
          type: 'category',
          data: [this.$t('message.y1'), this.$t('message.y2'), this.$t('message.y3'), this.$t('message.y4'), this.$t('message.y5')],
          axisLabel: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          }
        },
        yAxis: {
          type: 'value',
          name: 'km^2',
          axisLabel: {
            formatter: function (value) {
              return (value * value * value).toFixed(2)
            },
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
          nameTextStyle: {
            fontWeight: 'bold',
            color: 'rgb(0,0,0)',
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
        },
        legend: {
          textStyle: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          }
        },
        tooltip: {
          formatter: function (params) {
            // console.log(params)
            return (
              params.seriesName +
              '<br />' +
              params.name +
              ' ' +
              Math.abs(params.data * params.data * params.data)
            )
          }
        },
        toolbox: this.toolbox,
        series: this.areaBarSeries
      }
    },
    economicBarOption() {
      return {
        xAxis: {
          type: 'category',
          axisTick: {
            show: false
          },
          data: [this.$t('message.y1'), this.$t('message.y2'), this.$t('message.y3'), this.$t('message.y4'), this.$t('message.y5')],
          axisLabel: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          }
        },
        yAxis: [{
          type: 'value',
          name: this.$t('message.investAndBenefit'),
          nameGap: 30,
          nameLocation: 'middle',
          axisLabel: {
            formatter: function (value) {
              return Math.abs(value)
            },
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
          nameTextStyle: {
            fontWeight: 'bold',
            color: 'rgb(0,0,0)',
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
        },
        {
          type: 'value',
          name: this.$t('message.reductionRateAxisLabel'),
          nameGap: 20,
          nameLocation: 'middle',
          axisLabel: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
          nameTextStyle: {
            fontWeight: 'bold',
            color: 'rgb(0,0,0)',
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
          min: 0,
          max: 7,
          splitLine: {
            show: true,
            lineStyle: {
              type: 'dashed'
            }
          }
        },
        ],
        legend: {
          textStyle: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          }
        },
        tooltip: {
          formatter: function (params) {
            // console.log(params)
            return (
              params.seriesName +
              '<br />' +
              params.name +
              ' ' +
              Math.abs(params.data)
            )
          }
        },
        toolbox: this.toolbox,
        series: this.economicBarSeries
      }
    },
    economicBarOption2() {
      return {
        xAxis: {
          type: 'category',
          axisTick: {
            show: false
          },
          data: [this.$t('message.y1'), this.$t('message.y2'), this.$t('message.y3'), this.$t('message.y4'), this.$t('message.y5')],
          axisLabel: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          }
        },
        yAxis: //[
        {
          type: 'value',
          name: this.$t('message.investAndBenefit'),
          nameLocation: 'middle',
          nameGap: 30,
          axisLabel: {
            formatter: function (value) {
              return Math.abs(value * value * value)
            },
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
          nameTextStyle: {
            fontWeight: 'bold',
            color: 'rgb(0,0,0)',
            fontSize: 14,
            fontFamily: 'Times New Roman'
          },
        },
        //   {
        //     type: 'value',
        //     name: this.$t('message.reductionRateAxisLabel'),
        //     nameGap: 20,
        //     nameLocation: 'middle',
        //     axisLabel: {
        //       fontSize:14,
        //       fontFamily: 'Times New Roman'
        //     },
        //       nameTextStyle: {
        //         fontWeight: 'bold',
        //         color: 'rgb(0,0,0)',
        //         fontSize: 14,
        //         fontFamily: 'Times New Roman'
        //       },
        //       min:0,
        //       max:7,
        //       splitLine:{
        //         show: true,
        //         lineStyle:{
        //           type:'dashed'
        //         }
        //       }
        //   },
        // ],
        legend: {
          orient: 'vertical',
          itemGap: 5,
          height: '70px',
          textStyle: {
            fontSize: 14,
            fontFamily: 'Times New Roman'
          }
        },
        tooltip: {
          formatter: function (params) {
            // console.log(params)
            if (
              params.seriesIndex === 1 ||
              params.seriesIndex === 4 ||
              params.seriesIndex === 7 ||
              params.seriesIndex === 10
            ) {
              return (
                params.seriesName +
                '<br />' +
                params.name +
                ' ' +
                params.data.label
              )
            } else {
              return (
                params.seriesName +
                '<br />' +
                params.name +
                ' ' +
                Math.abs(params.data * params.data * params.data)
              )
            }
          }
        },
        toolbox: this.toolbox,
        series: this.economicBarSeries2
      }
    }
  }
}
</script>
<style>
.ivu-table .demo-table-info-row td {
  background-color: #2db7f5 !important;
  color: #fff !important;
}

.ivu-table .demo-table-info-bigger {
  color: rgb(255, 0, 0);
}

.ivu-table .demo-table-info-equal {
  color: rgb(52, 52, 52);
}

.ivu-table .demo-table-info-smaller {
  color: rgb(0, 252, 97);
}
</style>

<style lang="less" scoped>
@import "main.less";
</style>

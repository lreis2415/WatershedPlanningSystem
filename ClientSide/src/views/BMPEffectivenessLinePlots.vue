<template>
  <div>
    <Row>
      <Col>
        <v-chart class="chartA" :option="chartAOption"/>
      </Col>

      <Col>
        <v-chart class="chartA" :option="chartBOption"/>
      </Col>

      <Col>
        <v-chart class="chartA" :option="chartCOption"/>
      </Col>
    </Row>
    <Row>
      <Col>
        <v-chart class="chartA" :option="chartDOption"/>
      </Col>

      <Col>
        <v-chart class="chartA" :option="chartEOption"/>
      </Col>

      <Col>
        <v-chart class="chartA" :option="chartFOption"/>
      </Col>
    </Row>
  </div>
</template>

<script>
export default {
  name: 'BMPEffectivenessLinePlots',
  data () {
    return {
      xAxis: {
        type: 'value',
        name: 'Time',
        nameLocation: 'middle',
        nameGap: 5,
        nameTextStyle: {
          fontFamily: 'Microsoft YaHei',
          fontSize: 16,
          fontWeight: 'bold'
        },
        splitLine: {
          show: false
        },
        axisLabel: {
          show: false
        },
        axisTick: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        name: 'Effectiveness',
        nameRotate: 90,
        nameLocation: 'middle',
        nameGap: 5,
        nameTextStyle: {
          fontFamily: 'Microsoft YaHei',
          fontSize: 12,
          fontWeight: 'bold'
        },
        splitLine: {
          show: false
        },
        axisLabel: {
          show: false
        },
        axisTick: {
          show: false
        }
      },
      toolbox: {
        feature: {
          saveAsImage: {
            show: true,
            pixelRatio: 5
          }
        }
      },
      chartAOption: {},
      chartBOption: {},
      chartCOption: {},
      chartDOption: {},
      chartEOption: {},
      chartFOption: {}
    }
  },
  mounted () {
    this.chartA_linear()
    this.chartB_nonlinear()
    this.chartC_log()
    this.chartD_cubicOut()
    this.chartE_polynomial()
    this.chartF_logistic()
  }
  ,
  methods: {
    chartA_linear () {
      let data = []
      for (let i = 1; i < 50; i++) {
        let x = i * 0.1
        let y = 0.2 * x + 0.5
        data.push([x, y])
      }
      this.chartAOption = {
        title: {
          text: 'linear',
          show: false
        },
        xAxis: this.xAxis,
        yAxis: this.yAxis,
        toolbox: this.toolbox,
        series: [{
          type: 'line',
          data: data,
          symbol: 'none'
        }]
      }
    },

    chartB_nonlinear () {
      let data = []
      for (let i = 1; i < 50; i++) {
        let x = i * 0.1
        let y = 0
        if (x <= 1.5) {
          y = (1.06 * x + 1.9) / 1.49
        } else {
          y = (0.88 * x + 6.89) / 3.5
        }
        data.push([x, y])
      }
      this.chartBOption = {
        title: {
          text: 'nonlinear',
          show: false
        },
        xAxis: this.xAxis,
        yAxis: this.yAxis,
        toolbox: this.toolbox,
        series: [{
          type: 'line',
          data: data,
          symbol: 'none'
        }]
      }
    },
    chartD_cubicOut () {
      let data = []
      for (let i = 1; i < 50; i++) {
        let x = i * 0.1
        let y = x * x * x + 20
        data.push([x, y])
      }
      this.chartDOption = {
        title: {
          text: 'cubic',
          show: false
        },
        xAxis: this.xAxis,
        yAxis: this.yAxis,
        toolbox: this.toolbox,
        series: [{
          type: 'line',
          data: data,
          symbol: 'none'
        }]
      }
    },
    chartC_log () {
      let data = []
      for (let i = 1; i < 50; i++) {
        let x = i * 0.1
        let y = 2.5 * Math.log10(2.3 * x) + 2
        data.push([x, y])
      }
      this.chartCOption = {
        title: {
          text: 'log',
          show: false
        },
        xAxis: this.xAxis,
        yAxis: this.yAxis,
        toolbox: this.toolbox,
        series: [{
          type: 'line',
          data: data,
          symbol: 'none'
        }]
      }
    },
    chartE_polynomial () {
      let data = []
      for (let i = 1; i < 50; i++) {
        let x = i * 0.1
        let y = 0.05 * Math.pow(x, 3) - 0.33 * Math.pow(x, 2) + 0.76 * x + 0.2
        data.push([x, y])
      }
      this.chartEOption = {
        title: {
          text: 'polynomial',
          show: false
        },
        xAxis: this.xAxis,
        yAxis: this.yAxis,
        toolbox: this.toolbox,
        series: [{
          type: 'line',
          data: data,
          symbol: 'none'
        }]
      }
    },
    chartF_logistic () {
      let data = []
      for (let i = 1; i < 50; i++) {
        let x = i * 0.1
        let y = 1 + 4.3 * 1 / (1 + Math.pow(10.9, -x + 3))
        data.push([x, y])
      }
      this.chartFOption = {
        title: {
          text: 'logistic',
          show: false
        },
        xAxis: this.xAxis,
        yAxis: this.yAxis,
        toolbox: this.toolbox,
        series: [{
          type: 'line',
          data: data,
          symbol: 'none'
        }]
      }
    },
  }

}

</script>

<style scoped>

.chartA {
  width: 300px;
  height: 300px;
}

</style>

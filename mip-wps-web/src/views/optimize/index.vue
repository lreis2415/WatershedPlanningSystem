<template>
  <Layout>
    <Header>
      <BMPHeader @changeLag="changeLag"></BMPHeader>
    </Header>
    <Content>
      <div class="bmp-content">
        <Row :gutter="16" style="margin-bottom: 20px">
          <Col span="12">
            <Card :bordered="true">
              <p slot="title">
                <b>{{ $t("message.targetScenarioSpatial") }}</b>
              </p>
              <div id="map" ref="rootmap" style="height: 465px"></div>
              <img class="legend" src="@/assets/img/customized/legend.jpg" />
            </Card>
          </Col>
          <Col span="12">
            <div>
              <Card :bordered="true">
                <p slot="title">
                  <b>{{ $t("message.targetScenarioInfo") }}</b>
                </p>
                <Table stripe :columns="columns" :data="scenarioDataComp">
                  <template slot-scope="{ row }" slot="name">
                    <strong>{{ row.name }}</strong>
                  </template>
                </Table>
                <p>{{ $t("message.tableNotes") }}</p>
              </Card>
              <Button
                type="success"
                @click="goHistoryPage()"
                style="margin-top: 20px"
              >
                {{ $t("message.viewOptResults") }}
              </Button>
            </div>
          </Col>
        </Row>

        <Form
          class="mb20"
          :model="form"
          ref="form"
          :rules="rules"
          :label-width="150"
        >
          <FormItem style="display: none">
            <Input type="text" v-model="form.userGroupId"> </Input>
          </FormItem>
          <Row :gutter="16">
            <Col span="12">
              <Card :bordered="true">
                <p slot="title">
                  <b
                    ><span style="color: #ed4014">Step 1: </span
                    >{{ $t("message.setNameAndNotes") }}</b
                  >
                </p>
                <FormItem :label="$t('message.optName')" prop="name">
                  <Input
                    type="text"
                    v-model="form.name"
                    :placeholder="$t('message.plhdOptName')"
                    style="width: 400px"
                    @on-blur="checkName"
                  >
                  </Input>
                </FormItem>
                <FormItem :label="$t('message.optNotes')" prop="notes">
                  <Input
                    type="textarea"
                    v-model="form.notes"
                    :placeholder="$t('message.plhdOptNotes')"
                    style="width: 400px"
                  >
                  </Input>
                </FormItem>
              </Card>
              <Card :bordered="true" style="margin-top: 20px">
                <p slot="title">
                  <b
                    ><span style="color: #ed4014">Step 2:</span>
                    {{ $t("message.setStepwiseInvest") }}</b
                  >
                </p>
                <Tabs :animated="false">
                  <p class="tip">
                    {{ $t("message.lblSetInvestCaps") }}
                    <InputNumber
                      :max="300"
                      :min="200"
                      v-model="totalInvestmentCap"
                    />
                    <span style="margin-left: 10px; color: #ed4014"
                      >{{ investDiffText }}: {{ investDiffDisplay }}
                    </span>
                  </p>
                  <TabPane :label="$t('message.tabMultistageInvestCap')">
                    <Row :gutter="16">
                      <Col span="12">
                        <div>
                          <FormItem
                            class="fc-row"
                            :label="$t('message.firstYear')"
                            prop="invesments"
                            :label-width="100"
                          >
                            <Slider
                              v-model="form.invesments[0]"
                              @on-change="handleSliderChange"
                              show-input
                            ></Slider>
                          </FormItem>

                          <FormItem
                            class="fc-row"
                            :label="$t('message.secondYear')"
                            prop="invesments"
                            :label-width="100"
                          >
                            <Slider
                              v-model="form.invesments[1]"
                              @on-change="handleSliderChange"
                              show-input
                            ></Slider>
                          </FormItem>

                          <FormItem
                            class="fc-row"
                            :label="$t('message.thirdYear')"
                            prop="invesments"
                            :label-width="100"
                          >
                            <Slider
                              v-model="form.invesments[2]"
                              @on-change="handleSliderChange"
                              show-input
                            ></Slider>
                          </FormItem>

                          <FormItem
                            class="fc-row"
                            :label="$t('message.fourthYear')"
                            prop="invesments"
                            :label-width="100"
                          >
                            <Slider
                              v-model="form.invesments[3]"
                              @on-change="handleSliderChange"
                              show-input
                            ></Slider>
                          </FormItem>

                          <FormItem
                            class="fc-row"
                            :label="$t('message.fifthYear')"
                            prop="invesments"
                            :label-width="100"
                          >
                            <Slider
                              v-model="form.invesments[4]"
                              @on-change="handleSliderChange"
                              show-input
                            ></Slider>
                          </FormItem>
                          <FormItem
                            class="fc-row"
                            :label="$t('message.discountRate')"
                          >
                            <InputNumber
                              :max="20"
                              :min="0"
                              v-model="form.discountRate"
                            ></InputNumber>
                          </FormItem>
                        </div>
                      </Col>
                      <Col span="12">
                        <v-chart :option="barOption"></v-chart>
                      </Col>
                    </Row>
                  </TabPane>
                </Tabs>
              </Card>
            </Col>
            <Col span="12">
              <Card>
                <p slot="title">
                  <b
                    ><span style="color: #ed4014">Step 3: </span
                    >{{ $t("message.optAlgorithmParameters") }}</b
                  >
                </p>
                <Collapse v-model="value1">
                  <Panel name="1">
                    {{ $t("message.optObjectives") }}
                    <template #content>
                      <List border>
                        <ListItem
                          ><b>{{ $t("message.maxReductionRate") }}</b></ListItem
                        >
                        <ListItem
                          ><b>{{ $t("message.minNPV") }}</b></ListItem
                        >
                      </List>
                    </template>
                  </Panel>
                  <Panel name="2">
                    {{ $t("message.moAlgorithm") }}
                    <template #content>
                      <List border>
                        <ListItem><b>NSGA-II</b></ListItem>
                      </List>
                    </template>
                  </Panel>
                  <Panel name="3">
                    {{ $t("message.keyParameters") }}
                    <template #content>
                      <FormItem :label="$t('message.genNumbers')">
                        <InputNumber
                          :max="100"
                          :min="2"
                          v-model="form.generation"
                        ></InputNumber>
                        <Button type="primary" @click="resetParams()">{{
                          $t("message.reset")
                        }}</Button>
                      </FormItem>

                      <FormItem :label="$t('message.popSize')">
                        <InputNumber
                          :max="200"
                          :min="4"
                          :step="4"
                          v-model="form.population"
                        ></InputNumber>
                      </FormItem>
                      <FormItem :label="$t('message.crossoverRate')">
                        <InputNumber
                          :max="1"
                          :min="0"
                          :step="0.1"
                          v-model="form.crossoverRate"
                        ></InputNumber>
                      </FormItem>
                      <FormItem :label="$t('message.mutateRate')">
                        <InputNumber
                          :max="1"
                          :min="0"
                          :step="0.1"
                          v-model="form.mutateRate"
                        ></InputNumber>
                      </FormItem>
                      <FormItem :label="$t('message.selectRate')">
                        <InputNumber
                          :max="1"
                          :min="0"
                          :step="0.1"
                          v-model="form.selectRate"
                        ></InputNumber>
                      </FormItem>
                    </template>
                  </Panel>
                </Collapse>
              </Card>
              <Card style="margin-top: 20px">
                <p slot="title">
                  <b>{{ $t("message.submitOpt") }}</b>
                </p>
                <Row>
                  <Col span="7">
                    <Button
                      type="primary"
                      :loading="loadingOnline"
                      @click="handleSubmit('form', 'online')"
                      style="margin-top: 10px"
                    >
                      {{ $t("message.submitOptOnline") }}
                    </Button>
                  </Col>
                  <Col span="17">
                    {{ $t("message.optOnlineTip") }}
                  </Col>
                </Row>
                <Divider />
                <Row>
                  <Col span="7"
                    ><Button
                      type="primary"
                      :loading="loadingOffline"
                      @click="handleSubmit('form', 'offline')"
                      style="margin-top: 10px"
                    >
                      {{ $t("message.submitOptOffline") }}
                    </Button>
                  </Col>
                  <Col span="17">{{ $t("message.optOfflineTip") }} </Col>
                </Row>
              </Card>
            </Col>
          </Row>
          <!-- <div style="text-align: center;margin-top: 20px;">
            <Button type="primary" @click="handleSubmit('form')" class="mr20">
              {{ $t("message.submitOpt") }}
            </Button>
          </div> -->
        </Form>
      </div>
    </Content>
    <Footer>
      <BMPFooter></BMPFooter>
    </Footer>
  </Layout>
</template>

<script>
import "ol/ol.css";
import View from "ol/View";
import Map from "ol/Map";
import TileLayer from "ol/layer/Tile";
import { ZoomToExtent, defaults as defaultControls } from "ol/control";
import axios from "axios";
import BMPHeader from "@/components/header";
import BMPFooter from "@/components/footer";
import BingMaps from "ol/source/BingMaps";
import VectorSource from "ol/source/Vector";
import VectorLayer from "ol/layer/Vector";
import GeoJSON from "ol/format/GeoJSON";
import { Style, Fill, Stroke, Text } from "ol/style";
import * as olProj from "ol/proj";
import qs from "qs";

export default {
  name: "Home",
  data() {
    return {
      loadingOnline: false,
      loadingOffline: false,
      value1: ["1", "2", "3"],
      totalInvestmentCap: 230,
      unit_to_gene_indx: null,
      spUnitVectorLayer: null,
      selectedScenario: {},
      styleSeqCollection: [],
      investDiff: 0,
      investDiffDisplay: 0,
      investDiffText: this.$t("message.remaining"),
      form: {
        userId: localStorage.getItem("bmp_u_id"),
        userGroupId: localStorage.getItem("bmp_u_groupId"),
        name: "",
        stepwiseInvestMax: "",
        invesments: [46, 46, 46, 46, 46],
        objective1: this.$t("message.maxReductionRate"),
        objective2: this.$t("message.minNPV"),
        algorithm: this.$t("message.nsga"),
        bmpEffectType: "0",
        generation: 2,
        population: 4,
        crossoverRate: 0.8,
        selectRate: 0.8,
        mutateRate: 0.1,
        submitTime: "",
        discountRate: 10,
      },
      rules: {
        name: [
          {
            required: true,
            message: "Please fill in the name",
            trigger: "blur",
          },
        ],
      },
      map: null,

      scenarioData: [],
    };
  },
  created() {},
  mounted() {
    this.initMap();
    this.buildStyleSeqCollection();
    this.getSelectedScenario();
  },
  methods: {
    changeLag() {
      setTimeout(() => {
        this.$forceUpdate();
      }, 10);
    },
    initMap() {
      const baseMap = new TileLayer({
        preload: Infinity,
        source: new BingMaps({
          key: "AigZt6vqR3W3tJYQu_B2MV3Edi1OTYVF0E_bNg3HTNayCFoZzmDdBCDzmDOZP2WI",
          imagerySet: "AerialWithLabelsOnDemand",
        }),
      });

      const changtingCenter = olProj.transform(
        [116.46, 25.68],
        "EPSG:4326",
        "EPSG:3857"
      );

      const spUnitGeoJsonSource = new VectorSource({
        format: new GeoJSON(),
        url: "/api/customized/slppos_units.geojson",
      });

      this.spUnitVectorLayer = new VectorLayer({
        source: spUnitGeoJsonSource,
        style: new Style({
          fill: new Fill({
            color: "rgba(200,0,0,0.5)",
          }),
          // stroke: new Stroke({
          //   color: 'rgba(255,255,255,0.5)'
          // })
        }),
      });

      this.map = new Map({
        target: this.$refs.rootmap,
        controls: defaultControls().extend([
          new ZoomToExtent({
            label: "L",
            tipLabel: "locate to study area",
            extent: [
              12961004.992139718, 2957279.799988502, 12967530.803429564,
              2961722.702257578,
            ],
          }),
        ]),
        layers: [baseMap, this.spUnitVectorLayer],
        view: new View({
          center: changtingCenter,
          zoom: 14,
        }),
      });
      // console.log(this.map.getView().calculateExtent())
    },
    buildStyleSeqCollection() {
      const colorSeqs = [
        ["rgba(189,189,189,0.8)"],
        [
          "rgb(188,189,220)",
          "rgb(158,154,200)",
          "rgb(128,125,186)",
          "rgb(106,81,163)",
          "rgb(74,20,134)",
        ],
        [
          "rgb(199,233,192)",
          "rgb(161,217,155)",
          "rgb(116,196,118)",
          "rgb(49,163,84)",
          "rgb(0,109,44)",
        ],
        [
          "rgb(254,217,118)",
          "rgb(254,178,76)",
          "rgb(254,141,60)",
          "rgb(240,59,32)",
          "rgb(108,0,38)",
        ],
        [
          "rgb(198,219,239)",
          "rgb(158,202,225)",
          "rgb(107,174,214)",
          "rgb(49,130,189)",
          "rgb(8,81,156)",
        ],
      ];
      for (const cs of colorSeqs) {
        const styleSeq = [];
        for (const color of cs) {
          const temp = new Style({
            // default style
            fill: new Fill({
              color: color,
            }),
          });
          styleSeq.push(temp);
        }
        this.styleSeqCollection.push(styleSeq);
      }
    },
    getSelectedScenario() {
      axios
        .all([
          axios.get("/api/customized/unit_to_gene.json"),
          axios.get("/api/selectedScenario"),
        ])
        .then((resArr) => {
          const unit_to_gene_indx = resArr[0].data;
          this.selectedScenario = resArr[1].data;

          this.scenarioData = [
            {
              name: this.$t("message.fj"),
              area: this.selectedScenario.BMPS.FENGJIN.AREA.toFixed(2),
              money: this.selectedScenario.BMPS.FENGJIN.CAPEX.toFixed(2),
              money1: this.selectedScenario.BMPS.FENGJIN.OPEX.toFixed(2),
              money2: this.selectedScenario.BMPS.FENGJIN.INCOME.toFixed(2),
            },
            {
              name: this.$t("message.stlc"),
              area: this.selectedScenario.BMPS.SHENGTAILINCAO.AREA.toFixed(2),
              money: this.selectedScenario.BMPS.SHENGTAILINCAO.CAPEX.toFixed(2),
              money1: this.selectedScenario.BMPS.SHENGTAILINCAO.OPEX.toFixed(2),
              money2:
                this.selectedScenario.BMPS.SHENGTAILINCAO.INCOME.toFixed(2),
            },
            {
              name: this.$t("message.dxlgz"),
              area: this.selectedScenario.BMPS.DIXIAOLINGAIZAO.AREA.toFixed(2),
              money:
                this.selectedScenario.BMPS.DIXIAOLINGAIZAO.CAPEX.toFixed(2),
              money1:
                this.selectedScenario.BMPS.DIXIAOLINGAIZAO.OPEX.toFixed(2),
              money2:
                this.selectedScenario.BMPS.DIXIAOLINGAIZAO.INCOME.toFixed(2),
            },
            {
              name: this.$t("message.jjlg"),
              area: this.selectedScenario.BMPS.JINGJILINGUO.AREA.toFixed(4),
              money: this.selectedScenario.BMPS.JINGJILINGUO.CAPEX.toFixed(2),
              money1: this.selectedScenario.BMPS.JINGJILINGUO.OPEX.toFixed(2),
              money2: this.selectedScenario.BMPS.JINGJILINGUO.INCOME.toFixed(2),
            },
            {
              name: this.$t("message.summary"),
              area: this.selectedScenario.SUMMARY.AREA.toFixed(2),
              money: this.selectedScenario.SUMMARY.CAPEX.toFixed(2),
              money1: this.selectedScenario.SUMMARY.OPEX.toFixed(2),
              money2: this.selectedScenario.SUMMARY.INCOME.toFixed(2),
            },
          ];

          this.spUnitVectorLayer.setStyle((feature, res) => {
            const slpposUnit = feature.values_.DN;
            const geneIndex = unit_to_gene_indx[slpposUnit]; // slppos unit id => gene index
            const geneValue = this.selectedScenario.gene_values[geneIndex];
            if (geneValue === 0) {
              return this.styleSeqCollection[0][0];
            } else {
              const bmpType = Math.floor(geneValue / 1000);
              const implYear = geneValue % 1000;
              return this.styleSeqCollection[bmpType][5 - implYear];
            }
          });
        });
    },
    handleSubmit(name, mode) {
      if (this.investDiff > 0) {
        this.$Message.error(this.$t("message.investExceed"));
        return;
      }

      this.$refs[name].validate((valid) => {
        if (valid) {
          const root = this;
          this.form.stepwiseInvestMax = this.form.invesments.join(",");
          const date = new Date();
          const year = date.getFullYear();
          const month = date.getMonth() + 1;
          const day = date.getDate();
          const h = date.getHours();
          const m = date.getMinutes();
          const s = date.getSeconds();

          root.form.submitTime = `${year}-${month}-${day} ${h}:${m}:${s}`;
          root.form.investType = 1;
          root.form.stepwiseInvestMin = "0";
          root.form.mode = mode;

          if (mode == "online") {
            if (this.form.generation*this.form.population>20) {
              this.$Message.error(this.$t("message.parametersCannotOptOnline"));
              return;
            }
            this.loadingOnline = true;
            this.$Message.info(this.$t("message.startOptimizationOnline"));
          } else {
            this.loadingOffline = true;
          }

          const headers = {
            "content-type": "application/json",
          };

          let url =
            mode == "online"
              ? "/api/submitBMPOrdersOptimizationOnline"
              : "/api/submitBMPOrdersOptimizationOffline";
          axios
            .post(url, root.form, {
              headers: headers,
            })
            .then(function (res) {
              console.log(res);
              if (res.data && res.data.code == 1) {
                if (mode == "online") {
                  root.$Message.info({
                    content: root.$t("message.submitOptSuccessOnline"),
                    duration: 10,
                    closable: true,
                  });
                  root.loadingOnline = false;
                } else {
                  root.$Message.info({
                    content: root.$t("message.submitOptSuccessOffline"),
                    duration: 10,
                    closable: true,
                  });
                  root.loadingOffline = false;
                }
              } else {
                root.$Message.error(res.data.message);
                root.loadingOnline = false;
                root.loadingOffline = false;
              }
            })
            .catch(function (error) {
              console.log(error);
              root.loadingOnline = false;
              root.loadingOffline = false;
            });
        } else {
          this.$Message.error(this.$t("message.submitOptFailed"));
        }
      });
    },
    checkName() {
      const root = this;
      const name = this.form.name;
      const groupId = localStorage.getItem("bmp_u_groupId");
      if (groupId == null) {
        this.$Message.error(this.$t("message.loginError"));
        return;
      }
      axios(`/api/validateOptName?groupId=${groupId}&optName=${name}`).then(
        (res) => {
          if (res.data.code !== 1) {
            root.$Message.error(root.$t("message.optNameExists"));
            root.form.name = "";
          }
        }
      );
    },
    goHistoryPage() {
      const userName = localStorage.getItem("bmp_u_name");
      const email = localStorage.getItem("bmp_u_email");
      const userId = localStorage.getItem("bmp_u_id");
      const groupId = localStorage.getItem("bmp_u_groupId");
      const groupName = localStorage.getItem("bmp_u_groupName");
      const role = localStorage.getItem("bmp_u_role");
      if (groupId == null || userId == null) {
        this.$Message.error(this.$t("message.loginError"));
        return;
      }
      console.log(groupId, userId);
      axios(`/api/hasHistoricalOptimizations?groupId=${groupId}`).then(
        (res) => {
          if (res.data && res.data.code == 1) {
            location.href = "#/analysis";
          } else {
            this.$Message.error({
              content: this.$t("message.noOptHistory"),
              duration: 5,
            });
          }
        }
      );
    },
    handleSliderChange() {
      let sum = 0;
      for (let i = 0; i < 5; i++) {
        sum += this.form.invesments[i];
      }
      // console.log(sum)
      this.investDiff = sum - this.totalInvestmentCap;
      if (this.investDiff > 0) {
        this.investDiffText = this.$t("message.overrun");
      } else {
        this.investDiffText = this.$t("message.remaining");
      }
      this.investDiffDisplay = Math.abs(this.investDiff);
    },
    resetParams() {
      this.form.generation = 2;
      this.form.population = 4;
      this.form.crossoverRate = 0.8;
      this.form.selectRate = 0.8;
      this.form.mutateRate = 0.1;
    },
  },
  components: {
    BMPHeader,
    BMPFooter,
  },
  computed: {
    scenarioDataComp() {
      if (this.scenarioData.length) {
        this.scenarioData[0].name = this.$t("message.fj");
        this.scenarioData[1].name = this.$t("message.stlc");
        this.scenarioData[2].name = this.$t("message.dxlgz");
        this.scenarioData[3].name = this.$t("message.jjlg");
        this.scenarioData[4].name = this.$t("message.summary");
      }

      return this.scenarioData;
    },
    columns() {
      const data = [
        {
          title: " ",
          slot: "name",
          key: "index",
        },
        {
          title: `${this.$t("message.area")}`,
          key: "area",
        },
        {
          title: this.$t("message.initial"),
          key: "money",
        },
        {
          title: this.$t("message.maintain"),
          key: "money1",
        },
        {
          title: this.$t("message.benefit"),
          key: "money2",
        },
      ];
      return data;
    },
    barOption() {
      return {
        xAxis: {
          type: "category",
          data: [
            this.$t("message.firstYear"),
            this.$t("message.secondYear"),
            this.$t("message.thirdYear"),
            this.$t("message.fourthYear"),
            this.$t("message.fifthYear"),
          ],
          axisLabel: {
            fontSize: 14,
            fontFamily: "Times New Roman",
          },
        },
        yAxis: {
          type: "value",
          axisLabel: {
            fontSize: 14,
            fontFamily: "Times New Roman",
          },
        },
        series: [
          {
            data: this.form.invesments,
            type: "bar",
          },
        ],
      };
    },
  },
};
</script>

<style lang="less" scoped>
@import "main.less";
</style>

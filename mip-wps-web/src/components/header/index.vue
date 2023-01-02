<template>
  <div>
    <h1 class="bmp-tit">
      <p class="bmp-tit__text" :title="$t('message.title')">{{ $t("message.title") }}</p>
      <div class="user-wap">
        <div v-if="hasLogin">
          <div>
            <div class="user-name">
              {{ $t("message.userLabel") }}: {{ stakeholder.name }}
            </div>
            <div class="user-name">
              {{ $t("message.roleLabel") }}: {{ stakeholder.role }}
            </div>
            <div class="user-name">
              {{ $t("message.groupLabel") }}: {{ stakeholder.group }}
            </div>
          </div>

          <span class="user-menu" @click="logout">{{
            $t("message.logout")
          }}</span>
          <span @click="changeLanuage('cn')"
            ><Button type="text" ghost>中文</Button>|</span
          >
          <span @click="changeLanuage('en')"
            ><Button type="text" ghost>English</Button></span
          >
        </div>
        <div v-else>
          <div>
            <span class="user-menu" @click="showLogin = true">{{
              $t("message.login")
            }}</span>
            <span> | </span>
            <span class="user-menu" @click="showReg = true">{{
              $t("message.register")
            }}</span>
          </div>
        </div>
      </div>
    </h1>

    <Modal
      v-model="showLogin"
      :title="$t('message.login')"
      @on-ok="ok"
      ok-text="OK"
      cancel-text="Cancel"
    >
      <Form ref="formLogin" :model="formLogin" :rules="ruleLogin" inline>
        <FormItem prop="email">
          <Input type="text" v-model="formLogin.email" placeholder="Email">
            <Icon type="ios-mail" slot="prepend"></Icon>
          </Input>
        </FormItem>
        <FormItem prop="password">
          <Input
            type="password"
            v-model="formLogin.password"
            placeholder="Password"
          >
            <Icon type="ios-lock-outline" slot="prepend"></Icon>
          </Input>
        </FormItem>
      </Form>
    </Modal>

    <Modal
      v-model="showReg"
      :title="$t('message.register')"
      @on-ok="okReg"
      ok-text="OK"
      cancel-text="Cancel"
    >
      <Form ref="formReg" :model="formReg" :rules="ruleReg" :label-width="120">
        <FormItem :label="$t('message.nameLabel')" prop="name">
          <Input
            maxlength="10"
            v-model="formReg.name"
            placeholder="Please enter stakeholder name."
          ></Input>
        </FormItem>
        <FormItem :label="$t('message.emailLabel')" prop="email">
          <Input
            v-model="formReg.email"
            placeholder="Please enter email."
          ></Input>
        </FormItem>
        <FormItem :label="$t('message.passwordLabel')" prop="password">
          <Input
            type="password"
            v-model="formReg.password"
            placeholder="Please enter password."
          >
            <Icon type="ios-lock-outline" slot="prepend"></Icon>
          </Input>
        </FormItem>
        <FormItem :label="$t('message.groupLabel')" prop="userGroup">
          <Select
            placeholder="Please add newer group"
            v-model="formReg.groupId"
            filterable
            allow-create
            @on-create="handleAddGroup"
          >
            <Option
              v-for="item in groups"
              :value="item.value"
              :key="item.value"
              >{{ item.label }}</Option
            >
          </Select>
        </FormItem>
        <FormItem :label="$t('message.roleLabel')" prop="role">
          <Select v-model="formReg.role" style="width: 200px">
            <Option value="Government">Government</Option>
            <Option value="Enterprise">Enterprise</Option>
            <Option value="Citizen">Citizen</Option>
          </Select>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";

export default {
  name: "BMPHeader",
  data() {
    return {
      showLogin: false,
      showReg: false,
      hasLogin: false,
      stakeholder: null,
      groups: [],
      formLogin: {
        email: "",
        password: "",
      },
      formReg: {
        name: "",
        email: "",
        password: "",
        groupId: "",
        role: "Government",
      },
      ruleLogin: {
        email: [
          {
            required: true,
            message: "Please fill in the email",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "Please fill in the password.",
            trigger: "blur",
          },
          {
            type: "string",
            min: 6,
            message: "The password length cannot be less than 6 bits",
            trigger: "blur",
          },
        ],
      },
      ruleReg: {
        name: [
          {
            required: true,
            message: "Please fill in the name",
            trigger: "blur",
          },
        ],
        userGroup: [
          {
            required: true,
            message: "Please select or add new stakeholder group.",
            trigger: "blur",
          },
        ],
        email: [
          {
            required: true,
            message: "Please fill in the email",
            trigger: "blur",
          },
        ],
        password: [
          {
            required: true,
            message: "Please fill in the password.",
            trigger: "blur",
          },
          {
            type: "string",
            min: 6,
            message: "The password length cannot be less than 6 bits",
            trigger: "blur",
          },
        ],
        role: [
          {
            required: true,
            message: "Please select stakeholder role",
            trigger: "blur",
          },
        ],
      },
    };
  },
  mounted() {
    this.getStakeholderGroups();
  },
  methods: {
    handleAddGroup(val) {
      this.groups.push({
        value: val,
        label: val,
      });
      axios.get(`/api/addGroup?name=${val}`).then((res) => {
        if (res.data && res.data.code != 1) {
          this.$Message.error("Add new group failed!");
        }
      });
    },
    changeLanuage(type) {
      this.$emit("changeLag", type);
      this.$i18n.locale = type;
    },
    getStakeholderGroups() {
      axios.get("/api/stakeholderGroups").then((res) => {
        if (res.data && res.data.code == 1) {
          if (res.data.data && res.data.data.length) {
            this.groups = res.data.data.map((item) => {
              return {
                id: item.id,
                value: item.name,
                label: item.name,
              };
            });
          }
          this.initUser();
        } else {
          this.$Message.error("No stakeholder group!");
        }
      });
    },
    findGroupNameById(groupId) {
      console.log(groupId)
      for (let g of this.groups) {
        if (g.id == groupId) {
          return g.value;
        }
      }
      return "No group";
    },
    initUser() {
      const userName = localStorage.getItem("bmp_u_name");
      const email = localStorage.getItem("bmp_u_email");
      const userId = localStorage.getItem("bmp_u_id");
      const groupId = localStorage.getItem("bmp_u_groupId") || "";
      const groupName = localStorage.getItem("bmp_u_groupName");
      const role = localStorage.getItem("bmp_u_role");
      if (userName && email && userId && groupId && groupName && role) {
        this.hasLogin = true;
        this.stakeholder = {
          name: userName,
          id: userId,
          email: email,
          group: groupName,
          role: role,
        };
      } else {
        this.hasLogin = false;
      }
    },
    logout() {
      localStorage.clear();
      this.$Message.success(this.$t("message.exitSuccess"));
      location.href = "/";
    },
    ok() {
      let root = this;
      const headers = {
        "content-type": "application/x-www-form-urlencoded",
      };
      axios
        .post("/api/login", qs.stringify(root.formLogin), { headers: headers })
        .then(function (res) {
          if (res.data && res.data.code == 1) {
            root.$Message.success(root.$t("message.loginSuccess"));
            localStorage.setItem("bmp_u_id", res.data.data.id);
            localStorage.setItem("bmp_u_name", res.data.data.name);
            localStorage.setItem("bmp_u_email", res.data.data.email);
            localStorage.setItem("bmp_u_groupId", res.data.data.groupId);
            localStorage.setItem(
              "bmp_u_groupName",
              root.findGroupNameById(res.data.data.groupId)
            );
            localStorage.setItem("bmp_u_role", res.data.data.role);
            root.initUser();
          } else {
            root.$Message.error(res.data.message);
          }
        })
        .catch(function (error) {
          console.log(error);
        });
    },
    okReg() {
      const root = this;
      const headers = {
        "content-type": "application/x-www-form-urlencoded",
      };
      axios
        .post("/api/register", qs.stringify(root.formReg), { headers: headers })
        .then(function (res) {
          if (res.data && res.data.code == 1) {
            console.log(res.data.data);
            root.$Message.info(root.$t("message.registerSuccess"));
            localStorage.setItem("bmp_u_id", res.data.data.id);
            localStorage.setItem("bmp_u_name", res.data.data.name);
            localStorage.setItem("bmp_u_email", res.data.data.email);
            localStorage.setItem("bmp_u_groupId", res.data.data.groupId);
            localStorage.setItem(
              "bmp_u_groupName",
              root.findGroupNameById(res.data.data.groupId)
            );
            localStorage.setItem("bmp_u_role", res.data.data.role);
            location.href = "/";
          } else {
            root.$Message.error(res.data.message);
          }
        })
        .catch(function (error) {
          console.log(error);
        });
    },
  },
};
</script>

<style lang="less" scoped>
@import "main.less";
</style>

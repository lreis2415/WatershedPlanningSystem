<template>
  <Layout>
    <Header>
      <BMPHeader></BMPHeader>
    </Header>
    <Content>
      <div class="bmp-content">
        <div class="bmp-intro">
          <p class="bmp-block text">
            <span v-html='$t("message.intro1")'></span>
          </p>
          <p class="bmp-block text">
            <span v-html='$t("message.intro2")'></span>
          </p>
          <p class="bmp-block text">
            <span v-html='$t("message.intro3")'></span>
          </p>
          <p class="bmp-block">
          <div class="bmp-img"><img :src="bmpImg"></div>
          </p>
          <p class="bmp-block">
            <span v-html='$t("message.intro4")'></span>
          </p>
          <p>
          <div class="bmp-block">
            <Table stripe :columns="columns" :data="users"></Table>
          </div>
          </p>
          <p class="bmp-block">
            <span v-html='$t("message.intro5")'></span>
          </p>
          <p class="bmp-block">
            <span v-html='$t("message.intro6")'></span>
          </p>
        </div>
        <Button type="primary" class="mb20 btn-enter" @click="runDemo()">{{
    $t("message.runDemo")
          }}
        </Button>
      </div>
    </Content>

    <Footer>
      <BMPFooter></BMPFooter>
    </Footer>
  </Layout>
</template>

<script>
import BMPHeader from '@/components/header'
import BMPFooter from '@/components/footer'
import { resolve } from 'path';

export default {
  name: 'Home',
  data() {
    return {
      columns: [
        {
          title: 'Stakeholder',
          key: 'stakeholder'
        },
        {
          title: 'Email',
          key: 'email'
        },
        {
          title: 'Password',
          key: 'password'
        },
        {
          title: 'Group',
          key: 'group'
        },
        {
          title: 'Role',
          key: 'role'
        }
      ],
      users:[
        {
          stakeholder:'GovHill',
          email:'govhill@easygeoc.net',
          password:'123456',
          group:'EGCGroup',
          role:'Government'
        },
        {
          stakeholder:'ManagerSmith',
          email:'managersmith@easygeoc.net',
          password:'123456',
          group:'EGCGroup',
          role:'Enterprise'
        },
        {
          stakeholder:'James',
          email:'james@easygeoc.net',
          password:'123456',
          group:'EGCGroup',
          role:'Citizen'
        }
      ]
    }
  },
  created() { },
  mounted() { },
  methods: {
    runDemo() {
      const userName = localStorage.getItem('bmp_u_name')
      const email = localStorage.getItem('bmp_u_email')
      const userId = localStorage.getItem('bmp_u_id')
      const groupId = localStorage.getItem('bmp_u_groupId')
      const groupName = localStorage.getItem('bmp_u_groupName')
      const role = localStorage.getItem('bmp_u_role')
      if (userName && email && userId && groupId && groupName && role) {
        // location.href = '#/optimize'
        window.open('#/optimize')
      } else {
        this.$Message.error(this.$t('message.loginError'))
        return
      }
    }
  },
  components: {
    BMPHeader,
    BMPFooter
  },
  computed: {
    bmpImg() {
      const local = this.$i18n.locale;
      if (local === 'en') {
        return require('../../assets/img/customized/youwuzhen-bmps-en.jpg')
      } else {
        return require('../../assets/img/customized/youwuzhen-bmps-cn.jpg')
      }
    }
  }
}
</script>

<style lang="less" scoped>
@import "main.less";
</style>

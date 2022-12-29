<template>
  <div id="app" class="bmp">
    <router-view/>
  </div>
</template>

<style lang="less">
.ivu-row {
  .ol-scale-line {
    background: none;
  }
  .ol-scale-line-inner {
    color: #000;
    border: 1px solid #000;
    border-top: none;
    font-weight: bold;
    font-size: 20px;
  }
} 

.bmp {
  .ivu-layout-header {
    padding-top: 5px;
    padding-bottom: 5px;
    height:64px;
  }
}



</style>

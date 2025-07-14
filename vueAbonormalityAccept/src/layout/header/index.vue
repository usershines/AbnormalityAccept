
<template>
  <el-header>
    <div  style="background-color:#3490de;border-radius: 10px;
      position: fixed; top:0; left: 0;width: 100%;height: 110px;
      z-index: 99;display: flex;">
      <div style="width: 160px; padding: 10px;cursor: pointer" @click=clickLogo >
        <el-image :src=logo fit="contain" />
      </div>
      <div style="margin: auto 10px -10px;">
        <h3>感谢您为人类做出的贡献</h3>
      </div>
      <div style="width: auto; margin: 10px 10px 10px auto" >
        <el-badge :value="12" class="item" style="margin: 10px 10px 50px;">
          <el-button size="large" @click="clickWork">工作区</el-button>
        </el-badge>
        <el-badge :value="12" class="item" style="margin: 10px 10px 50px;">
          <el-button size="large" @click="clickEmail">邮箱</el-button>
        </el-badge>
        <el-avatar  :size="80" :src="userAvatar" />
      </div>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const logo = 'src/assets/pic/logo.png'
const userAvatar = ref("https://ts3.tc.mm.bing.net/th/id/OIP-C.UpIFaAcZ-SrEPtvYmhWOMwHaHa?w=249&h=250&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2")
const clickLogo = () =>{
  console.log("返回主页")
}
const clickWork = () =>{
  console.log("打开工作区")
}
const clickEmail = () =>{
  console.log("打开邮箱")
}

</script>


<style scoped>

</style>
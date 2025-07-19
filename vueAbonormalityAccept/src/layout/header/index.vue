<template>
    <div  style="background-color:#3490de;border-radius: 10px;
      position: fixed; top:0; left: 2.5%;width: 95%;height: 110px;
      z-index: 99;display: flex;">
      <div style="width: 160px; padding: 10px;cursor: pointer" @click=goToHome >
        <el-image :src=logo fit="contain" />
      </div>
      <div style="display: flex">
        <el-text style="font-size: 80px;color: #fafafa;margin: auto 5px 5px 5px"><strong>{{currentHour}}:{{currentMinutes}}</strong></el-text>
        <el-text style="font-size: 30px;margin: auto 10px 5px 0px;color: #fafafa">:{{currentSeconds}}</el-text>
      </div>
      <div style="margin: auto 10px -10px;">
        <h3>感谢您为人类做出的贡献</h3>
      </div>
      <div style="width: auto; margin: 10px 10px 10px auto;padding: 10px 10px 50px;display: flex" >
        <el-menu
            :default-active="defaultActive"
            style="position: relative;bottom: -30px;margin-right: 30px;border-radius: 10px 10px 0 0 "
            mode="horizontal"
            router
        >
          <el-menu-item index="/start" @click="goToHome" >首页</el-menu-item>
          <el-menu-item index="/workPlace">工作区</el-menu-item>
          <el-menu-item index="/email/inbox">邮箱</el-menu-item>
          <el-menu-item index="order">Orders</el-menu-item>
        </el-menu>
        <el-avatar  :size="80" :src="userAvatar" />
        <el-button @click="Logout">登出</el-button>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch} from 'vue'
import router from "@/router";
import {useRoute} from "vue-router";
import {logout} from "@/api/user.ts";

// 获取当前路由
const route = useRoute()
const defaultActive = ref('')
console.log(defaultActive.value)

//路由变化时更新
// 更新 defaultActive 的函数
const updateDefaultActive = () => {
  if (route.path.startsWith('/workPlace')) {
    defaultActive.value = '/workPlace';
  } else {
    if (route.path.startsWith('/email')) {
      defaultActive.value = '/email/inbox';
    }else {
      defaultActive.value = route.name;
    }
  }
};

const logo = '/src/assets/pic/logo.png'
const userAvatar = ref("https://ts3.tc.mm.bing.net/th/id/OIP-C.UpIFaAcZ-SrEPtvYmhWOMwHaHa?w=249&h=250&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2")
const goToHome = () =>{
  console.log("返回主页")
  router.push({name: '/start'})
}
// 时钟
const currentHour = ref()
const currentMinutes = ref()
const currentSeconds = ref('')
let interval: number | undefined
const updateTime = () => {
  const now = new Date()
  currentHour.value = now.getHours().toString().padStart(2,'0')
  currentMinutes.value = now.getMinutes().toString().padStart(2,'0')
  currentSeconds.value = now.getSeconds().toString().padStart(2,'0')
}
// 生命周期钩子：创建时开始更新时间
onMounted(() => {
  // 路由更新
  updateDefaultActive();
  watch(
      () => route.path,
      () => {
        updateDefaultActive();
      }
  );

  updateTime() // 初始化时间
  interval = setInterval(updateTime, 1000) // 每秒更新一次
})
// 生命周期钩子：卸载前清除定时器
onBeforeUnmount(() => {
  if (interval !== undefined) {
    clearInterval(interval)
  }
})

// 登出
const Logout = () => {
  ElMessage.info('正在登出')
  logout().then((res) => {
    if(res.code === 200){
      router.push("/login")
      localStorage.clear()
    }
  }).catch(e => {
    ElMessage.error(e.msg)
    console.log(e)
  }
  )
}

</script>


<style scoped>
.item{
  margin: 30px 10px 10px;
}

.el-button{
  width: 70px;
  height: 40px;
  font-size: 20px;
}
</style>
<template >
  <div><el-container>
    <Header/>
    <el-main style="padding-top: 110px;display: flex;align-items: center">
      <router-view style="margin: 10px 3% 10px 3% "/>
    </el-main>
  </el-container></div>
</template>

<script setup lang="ts">
import Header from '../layout/header/index.vue'

</script>


<style>
body{
  background: #cbf1f5;
}


</style>
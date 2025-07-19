<template>
  <el-container>
    <Aside/>
    <el-main><router-view/></el-main>
  </el-container>
</template>

<script setup lang="ts">
import Aside from '@/views/workPlace/menu/index.vue'
</script>


<style scoped>

</style>
<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <RouterView :key="$route.fullPath" />
</template>

<style scoped></style>

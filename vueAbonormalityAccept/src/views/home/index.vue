<template>
  <el-card>
    <template #header>
    <el-carousel
        height="200px"
        motion-blur
        type="card"
        ref="carousel"
    >
      <el-carousel-item v-for="image in carouselImages" :key="image" @click="clickCarousel(image)">
        <el-image :src=image.url />
      </el-carousel-item>
    </el-carousel>
  </template>

  <template #default>
    <el-container>
      <el-aside style="width: auto">
        <el-card>
          <h3 style="text-align: center">异想体</h3>
          <el-table
              :data="tableData"
              style="width: 100%;height: 600px"
              :row-class-name="tableRowClassName"
          >
            <el-table-column prop="num" label="编号" width="180" />
            <el-table-column prop="name" label="名称" width="180" />
          </el-table>
        </el-card>
      </el-aside>
      <el-main>
        <el-calendar>
          <template #date-cell="{ data }">
            <p :class="data.isSelected ? 'is-selected' : ''">
              {{ data.day.split('-').slice(2).join('-') }}
              {{ data.isSelected ? '✔️' : '' }}
            </p>
          </template>
        </el-calendar>
      </el-main>
    </el-container>
  </template>

  <template #footer>
    <div style="display: flex; justify-content: flex-end">
      <h4 style="text-align: right">收容 控制 保护</h4>
    </div>
  </template></el-card>

</template>

<script setup lang="ts">
import {ref} from "vue";

interface abnormality {
  id: number
  num: number
  level: number
  name: string
}

const tableRowClassName = ({
                             row,
                           }: {
  row: abnormality
}) => {
  switch (row.level) {
    case 0 : return 'safe'
    case 1 : return ''
    case 2 : return 'warning'
    case 3 : return 'danger'
  }
}

const tableData: abnormality[] = [
  {
    id: 1,
    num: 1,
    level: 1,
    name: 'normal',
  },
  {
    id: 1,
    num: 1,
    level: 0,
    name: 'safe',
  },
  {
    id: 1,
    num: 1,
    level: 2,
    name: 'warn',
  },{
    id: 1,
    num: 1,
    level: 3,
    name: 'danger',
  }
]

const carousel= ref()
const carouselImages=ref({
  Image1: {
    index: 1,
    url: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.NyFThvIJGMEXsFqsJDoh7gHaEK?w=333&h=187&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',
  },
  Image2: {
    index: 2,
    url: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.NyFThvIJGMEXsFqsJDoh7gHaEK?w=333&h=187&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',
  },
  Image3: {
    index: 3,
    url: 'https://ts3.tc.mm.bing.net/th/id/OIP-C.NyFThvIJGMEXsFqsJDoh7gHaEK?w=333&h=187&c=8&rs=1&qlt=90&o=6&dpr=1.5&pid=3.1&rm=2',
  },
})

const clickCarousel= (image: any) =>{
  console.log("打开图片id为"+image.index+"的页面")
}

</script>

<style>
.el-carousel__item{
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-table .warning {
  background-color: #E6A23C !important;
}
.el-table .danger {
  background-color: #F56C6C !important;
}
.el-table .safe {
  background-color: #67C23A !important;
}
</style>
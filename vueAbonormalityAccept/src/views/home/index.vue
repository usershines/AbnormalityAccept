<template>
  <el-card style="height: 770px; background-color: #f9f9f9; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);">
    <template #header>
      <el-carousel
          height="200px"
          motion-blur
          type="card"
          ref="carousel"
          style="background-color: #add8e6;"
          :width="600"

      >
        <el-carousel-item v-for="notice in notices" :key="notice" @click="clickCarousel(notice)">
          <h2 style="color: #333; font-size: 24px; font-weight: 600;">{{notice.theme}}</h2>
        </el-carousel-item>
      </el-carousel>
    </template>

    <template #default>
      <el-container>
        <el-card shadow="hover" class="tableCard" style="background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);">
          <div class="cardHeader">
            <div class="color-legend">
              <span class="legend-item warning">初级</span>
              <span class="legend-item danger">中级</span>
              <span class="legend-item info">高级</span>
              <span class="legend-item safe">O5权限</span>
            </div>
            <h3 style="text-align: center; margin-left: 130px; color: #333; font-size: 20px; font-weight: 600;">用户</h3>
            <div class="avatar">
              <el-image :src="userAvatar" style="border-radius: 50%; overflow: hidden;" />
            </div>
          </div>
          <el-table
              :data="userTableData"
              style="width: 100%;height: 600px; border-radius: 8px; overflow: hidden;"
              :row-class-name="userColor"
          >
            <el-table-column prop="username" label="用户名" width="180" />
            <el-table-column prop="level" label="权限等级" width="180" />
          </el-table>
        </el-card>

        <el-card shadow="hover" class="tableCard" style="background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);">
          <div class="cardHeader">
            <div class="color-legend">
              <span class="legend-item info">无效化</span>
              <span class="legend-item safe">安全</span>
              <span class="legend-item warning">未解明</span>
              <span class="legend-item danger">灭世/机密</span>
            </div>
            <h3 style="text-align: center; margin-left: 110px; color: #333; font-size: 20px; font-weight: 600;">异想体</h3>
            <div class="avatar">
              <el-image :src="abnormalityAvatar" style="border-radius: 50%; overflow: hidden;" />
            </div>
          </div>
          <el-table
              :data="abnormalityTableData"
              style="width: 100%;height: 600px; border-radius: 8px; overflow: hidden;"
              :row-class-name="abnormalityColor"
          >
            <el-table-column prop="name" label="名称" width="180" />
            <el-table-column prop="level" label="级别" width="180" />
          </el-table>
        </el-card>

        <el-card shadow="hover" class="tableCard" style="background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);">
          <div class="cardHeader">
            <div class="color-legend">
              <span class="legend-item safe">空闲</span>
              <span class="legend-item warning">任务中</span>
              <span class="legend-item danger">无法行动</span>
              <span class="legend-item info">未知</span>
            </div>
            <h3 style="text-align: center; margin-left: 100px; color: #333; font-size: 20px; font-weight: 600;">机动小队</h3>
            <div class="avatar">
              <el-image :src="teamAvatar" style="border-radius: 50%; overflow: hidden;" />
            </div>
          </div>
          <el-table
              :data="teamTableData"
              style="width: 100%;height: 600px; border-radius: 8px; overflow: hidden;"
              :row-class-name="teamColor"
          >
            <el-table-column prop="name" label="名称" width="180" />
            <el-table-column prop="status" :formatter="teamStatus" label="状态" width="180" />
          </el-table>
        </el-card>

        <el-card shadow="hover" class="tableCard" style="background-color: #fff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);">
          <div class="cardHeader">
            <div class="color-legend">
              <span class="legend-item info">废弃</span>
              <span class="legend-item safe">常规</span>
              <span class="legend-item warning">临时</span>
              <span class="legend-item danger">重要</span>
            </div>
            <h3 style="text-align: center; margin-left: 130px; color: #333; font-size: 20px; font-weight: 600;">设施</h3>
            <div class="avatar">
              <el-image :src="facilityAvatar" style="border-radius: 50%; overflow: hidden;" />
            </div>
          </div>
          <el-table
              :data="facilityTableData"
              style="width: 100%;height: 600px; border-radius: 8px; overflow: hidden;"
              :row-class-name="facilityColor"
          >
            <el-table-column prop="facilityName" label="名称" width="180" />
            <el-table-column prop="facilityAddress" show-overflow-tooltip label="地址" width="250" />
          </el-table>
        </el-card>
      </el-container>
    </template>

    <template #footer>
      <div style="display: flex; justify-content: flex-end">
        <h4 style="text-align: right; color: #666; font-size: 14px;">收容 控制 保护</h4>
      </div>
    </template>
  </el-card>

  <el-dialog
      v-model="noticeVisible"
      class = "notice-container"
      style="background-color: #fff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"
  >
    <div style="height: 500px;">
      <div style="align-items: center;display: flex;flex-direction: column">
        <div class="notice-header">
          <h1 class="notice-title" style="font-size: 36px; font-weight: 700; color: #333;">{{ currentNotice?.theme }}</h1>
          <div class="title-divider" style="background: linear-gradient(90deg, transparent, #e84545, transparent);"></div>
        </div>

        <pre class="notice-content" v-html="currentNotice?.content" style="color: #666; font-size: 16px;"></pre>
      </div>
    </div>
    <div>
      <h3 style="margin-left: 600px; color: #666; font-size: 14px;">发布时间：{{formatDate(currentNotice?.time.toString())}}</h3>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import {onMounted, ref} from "vue";
import {findAllNotice} from "@/api/notice.ts";
import type {Notice} from "@/api/notice.ts";
import {getAbnormalityList} from "@/api/abnormality.ts";
import {getUserList} from "@/api/user.ts";
import {getFacilityList} from "@/api/facility.ts";
import {getTeamList} from "@/api/team.ts";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";

const noticeVisible = ref(false);

const userAvatar = ref("/src/assets/pic/user.png");
const abnormalityAvatar = ref("/src/assets/pic/abnormality.png");
const teamAvatar = ref("/src/assets/pic/team.png");
const facilityAvatar = ref("/src/assets/pic/facility.png");

const abnormalityColor = (row) => {
  switch (row.row.level) {
    case 1 : return 'info'
    case 2 : return 'safe'
    case 3 : return 'warning'
    case 4 : return 'danger'
    case 5 : return 'danger'
  }
}

// 表格相关数据
const abnormalityTableData= ref([
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
  },
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
])
const userTableData = ref([
  {
    id: 1,
    username: 'john_doe',
    password: 'hashed_password_1',
    email: 'john@example.com',
    inviterId: null,
    leaderId: 5,
    facilityId: 101,
    introduction: '系统管理员',
    level: 5
  },
  {
    id: 2,
    username: 'jane_smith',
    password: 'hashed_password_2',
    email: 'jane@example.com',
    inviterId: 1,
    leaderId: 5,
    facilityId: 101,
    introduction: '高级用户',
    level: 4
  },
  {
    id: 3,
    username: 'alex_jones',
    password: 'hashed_password_3',
    email: 'alex@example.com',
    inviterId: 2,
    leaderId: 5,
    facilityId: 102,
    introduction: '普通用户',
    level: 3
  },
  {
    id: 4,
    username: 'sarah_wilson',
    password: 'hashed_password_4',
    email: 'sarah@example.com',
    inviterId: 2,
    leaderId: 6,
    facilityId: 102,
    introduction: '普通用户',
    level: 3
  },
  {
    id: 5,
    username: 'mike_brown',
    password: 'hashed_password_5',
    email: 'mike@example.com',
    inviterId: null,
    leaderId: null,
    facilityId: 101,
    introduction: '部门领导',
    level: 4
  },
  {
    id: 6,
    username: 'emily_davis',
    password: 'hashed_password_6',
    email: 'emily@example.com',
    inviterId: 5,
    leaderId: null,
    facilityId: 102,
    introduction: '部门领导',
    level: 4
  },
  {
    id: 7,
    username: 'david_miller',
    password: 'hashed_password_7',
    email: 'david@example.com',
    inviterId: 6,
    leaderId: 6,
    facilityId: 102,
    introduction: '初级用户',
    level: 2
  },
  {
    id: 8,
    username: 'olivia_walker',
    password: 'hashed_password_8',
    email: 'olivia@example.com',
    inviterId: 3,
    leaderId: 5,
    facilityId: 101,
    introduction: '初级用户',
    level: 2
  }
])
const teamTableData = ref([
  {
    id: 1,
    name: "先锋队",
    members: [101, 102, 103],
    status: 1,
    resolvingQuestId: 1001,
    level: 5,
    description: 1,
    leaderId: 101
  },
  {
    id: 2,
    name: "守护者联盟",
    members: [104, 105, 106, 107],
    status: 1,
    resolvingQuestId: 1002,
    level: 4,
    description: 2,
    leaderId: 104
  },
  {
    id: 3,
    name: "探索者",
    members: [108, 109],
    status: 0,
    resolvingQuestId: 0,
    level: 3,
    description: 3,
    leaderId: 108
  },
  {
    id: 4,
    name: "暗影",
    members: [110, 111, 112],
    status: 1,
    resolvingQuestId: 1003,
    level: 5,
    description: 4,
    leaderId: 110
  },
  {
    id: 5,
    name: "光明之翼",
    members: [113, 114, 115, 116],
    status: 2,
    resolvingQuestId: 0,
    level: 4,
    description: 5,
    leaderId: 113
  },
  {
    id: 6,
    name: "钢铁之师",
    members: [117, 118, 119],
    status: 1,
    resolvingQuestId: 1004,
    level: 3,
    description: 6,
    leaderId: 117
  },
  {
    id: 7,
    name: "雷霆",
    members: [120, 121, 122, 123],
    status: 1,
    resolvingQuestId: 1005,
    level: 5,
    description: 7,
    leaderId: 120
  },
  {
    id: 8,
    name: "星辰",
    members: [124, 125, 126],
    status: 0,
    resolvingQuestId: 0,
    level: 4,
    description: 8,
    leaderId: 124
  }
])
const facilityTableData = ref([
  {
    id: 1,
    facilityName: "总部大厦",
    facilityAddress: "北京市朝阳区科技园区88号",
    level: 5,
    managerId: 201
  },
  {
    id: 2,
    facilityName: "南方基地",
    facilityAddress: "广州市天河区创新路120号",
    level: 4,
    managerId: 202
  },
  {
    id: 3,
    facilityName: "西部研发中心",
    facilityAddress: "成都市高新区科技大道56号",
    level: 0,
    managerId: 203
  },
  {
    id: 4,
    facilityName: "北方仓库",
    facilityAddress: "哈尔滨市南岗区工业路78号",
    level: 3,
    managerId: 204
  },
  {
    id: 5,
    facilityName: "华东分部",
    facilityAddress: "上海市浦东新区张江高科技园区32号",
    level: 4,
    managerId: 205
  },
  {
    id: 6,
    facilityName: "中部训练基地",
    facilityAddress: "武汉市武昌区光谷大道45号",
    level: 3,
    managerId: 206
  },
  {
    id: 7,
    facilityName: "西南前哨站",
    facilityAddress: "昆明市五华区科技路67号",
    level: 2,
    managerId: 207
  },
  {
    id: 8,
    facilityName: "西北观察站",
    facilityAddress: "西安市雁塔区创新二路99号",
    level: 2,
    managerId: 208
  }
])

// 表格数据转化
const teamStatus = (row) =>{
  switch (row.status){
    case 0: return '空闲'
    case 1: return '任务中'
    case 2: return '无法行动'
    case 3: return '未知'
  }
}
const teamColor = (row) =>{
  switch (row.row.status){
    case 0: return 'safe'
    case 1: return 'warning'
    case 2: return 'danger'
    case 3: return 'info'
  }
}
const facilityColor = (row) =>{
  switch (row.row.level){
    case 1: return 'info'
    case 2: return 'safe'
    case 3: return 'warning'
    case 4: return 'danger'
    case 5: return 'danger'
  }
}
const userColor = (row) =>{
  switch (row.row.level){
    case 5: return 'safe'
    case 1: return 'warning'
    case 2: return 'danger'
    case 3: return 'info'
    case 4: return 'info'
  }
}

const carousel= ref()

const notices = ref<Notice[]>([
  {
    id: 1,
    theme: '1111',
    content: '23333',
    time: new Date(),
    state: 1,
  },
  {
    id: 1,
    theme: '222',
    content: '23333\n\n\n\n\n12211\n\n\n\n122312331\n\n\n\n23232\n\n\n\n23233223',
    time: new Date(),
    state: 1,
  },
])
const currentNotice = ref<Notice>()

const catchNotices = () =>{
  findAllNotice().then((res) =>{
    if(res.code === 200){
      console.log(res)
      notices.value = res.data.list
      ElMessage.success('公告更新成功')
    }
  }).catch((err) =>{
    console.log(err)
    ElMessage.error('公告更新失败'+err.msg)
  })
}

const clickCarousel= (notice: Notice) =>{
  console.log("打开图片id为"+notice.id+"的页面")
  currentNotice.value = {
    ...notice,
  };
  console.log(currentNotice.value)
  noticeVisible.value = true
}

// 获取异想体列表
const catchAbnormality = () =>{
  getAbnormalityList(1,8).then(res=>{
    console.log(res)
    if(res.code === 200){
      abnormalityTableData.value = res.data.list
      ElMessage.success('异想体系统连接成功')
    }
  }).catch((err) =>{
    console.log(err)
    ElMessage.error('异想体系统错误'+err.msg)
  })
}

// 获取用户列表
const catchUser = () =>{
  getUserList(1,8).then(res=>{
    console.log(res)
    if(res.code === 200){
      userTableData.value = res.data.list
      ElMessage.success('用户系统连接成功')
    }
  }).catch((err) =>{
    console.log(err)
    ElMessage.error('用户系统错误'+err.msg)
  })
}

// 获取设施列表
const catchFacility = () =>{
  getFacilityList(1,8).then(res=>{
    console.log(res)
    if(res.code === 200){
      facilityTableData.value = res.data.list
      ElMessage.success('设施系统连接成功')
    }
  }).catch((err) =>{
    console.log(err)
    ElMessage.error('设施系统错误:'+err.msg)
  })
}

//获取队伍列表
const catchTeam = () =>{
  getTeamList(1,8).then(res=>{
    console.log(res)
    if(res.code === 200){
      teamTableData.value = res.data.list
      ElMessage.success('队伍系统连接成功')
    }
  }).catch((err) =>{
    console.log(err)
    ElMessage.error('队伍系统错误'+err.msg)
  })
}

// 生命周期钩子相关
onMounted(() =>{
  catchNotices()
  catchAbnormality()
  catchUser()
  catchFacility()
  catchTeam()
})

// 时间显示
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  } else if (diffDays === 1) {
    return '昨天'
  } else if(now.getFullYear()===date.getFullYear()){
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
  }else{
    return date.toLocaleDateString([], {year:'numeric', month: 'short', day: 'numeric' })
  }
}

</script>

<style>
.el-carousel__item{
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-calendar-table .el-calendar-day{
  height: 60px;
}

.el-table .warning {
  background-color: #ffd700 !important;
}
.el-table .danger {
  background-color: #ff6347 !important;
}
.el-table .safe {
  background-color: #90ee90 !important;
}
.el-table .info{
  background-color: #add8e6 !important;
}

.color-legend {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.legend-item {
  padding: 2px 5px;
  border-radius: 3px;
  font-size: 12px;
}

.legend-item.safe {
  background-color: #90ee90;
}

.legend-item.warning {
  background-color: #ffd700;
}

.legend-item.danger {
  background-color: #ff6347;
}

.legend-item.info {
  background-color: #add8e6;
}
</style>

<style scoped>
.title-divider {
  height: 3px;
  width: 300px;
  border-radius: 3px;
}

.notice-container {
  width: 700px;
  height: 500px;
  display:  flex;
  flex-flow: column;
  align-items: center;
}

.notice-content {
  height: 300px;
  width: 600px;
  font-size: 20px;
  overflow-y: auto;
  line-height: 1.9;
  position: relative;
}

.notice-title {
  font-weight: 700;
  margin: 0;
  letter-spacing: -0.5px;
  position: relative;
  display: inline-block;
  padding: 0 15px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.notice-header {
  text-align: center;
  margin-bottom: 40px;
  position: relative;
}

.tableCard{
  margin: 10px;
  position: relative;
}

.avatar{
  height: 60px;
  width:  60px;
}

.cardHeader{
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}
</style>
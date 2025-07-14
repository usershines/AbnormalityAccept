<template>
  <!-- 搜索表单区域 -->
  <el-form :model="searchForm" inline style="margin-bottom: 16px;">
    <el-form-item label="姓名">
      <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
    </el-form-item>

    <el-form-item label="权限等级">
      <el-select v-model="searchForm.level" placeholder="请选择权限等级" clearable>
        <el-option label="O5议会" value="O5"></el-option>
        <el-option label="A级" value="A"></el-option>
        <el-option label="B级" value="B"></el-option>
        <el-option label="C级" value="C"></el-option>
        <el-option label="D级" value="D"></el-option>
      </el-select>
    </el-form-item>

    <el-form-item label="所在地">
      <el-input
        v-model="searchForm.location"
        placeholder="请输入所在地"
        clearable
      ></el-input>
    </el-form-item>

    <el-form-item label="id">
      <el-input v-model="searchForm.id" placeholder="请输入id" clearable></el-input>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </el-form-item>
  </el-form>

  <!-- 数据表格区域 -->
  <el-table :data="tableData" border style="width: 100%">
    <el-table-column prop="id" label="id"></el-table-column>
    <el-table-column prop="name" label="名称"></el-table-column>
    <el-table-column prop="level" label="权限等级"></el-table-column>
    <el-table-column prop="location" label="所在地"></el-table-column>
    <el-table-column prop="superior" label="上级"></el-table-column>
    <el-table-column prop="description" label="简介"></el-table-column>
    <el-table-column label="操作" width="180">
      <template #default="scope">
        <el-button type="text" @click="handleDetail(scope.row)">详情</el-button>
        <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
        <el-button type="text" style="color: #F56C6C" @click="handleDelete(scope.row)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页组件区域 -->
  <el-pagination
    @size-change="handleSizeChange"
    @current-change="handleCurrentChange"
    :current-page="currentPage"
    :page-sizes="[10, 20, 30]"
    :page-size="pageSize"
    layout="total, sizes, prev, pager, next, jumper"
    :total=total
    prev-text="上一页"
    next-text="下一页"
    style="margin-top: 16px;"
  >
  </el-pagination>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import {getUserList} from "@/api/user.ts";

// 搜索表单数据
const searchForm = reactive({
  name: '',
  level: '',
  location: '',
  id: ''
});

// 原始表格数据（静态）
/*
const originTableData = ref([
  {
    id: 1,
    name: 'O5-13',
    level: 'O5',
    email: 'o5-13@scp-foundation.org',
    location: 'Site-19',
    superior: 'O5议会',
    status: 1,
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    description: '监督者议会成员'
  },
  {
    id: 2,
    name: 'Dr. Bright',
    level: 'A',
    email: 'bright@scp-foundation.org',
    location: 'Site-19',
    superior: 'O5-13',
    status: 1,
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    description: '高级研究员，SCP-963持有者'
  },
  {
    id: 3,
    name: 'Dr. Clef',
    level: 'A',
    email: 'clef@scp-foundation.org',
    location: 'Site-17',
    superior: 'O5-7',
    status: 1,
    avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
    description: '异常实体分析专家'
  },
  {
    id: 4,
    name: 'Dr. Gears',
    level: 'A',
    email: 'gears@scp-foundation.org',
    location: 'Site-19',
    superior: 'O5-13',
    status: 0,
    avatar: 'https://cube.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6dpng.png',
    description: '机械工程与异常收容专家'
  },
  {
    id: 5,
    name: 'MTF Epsilon-11',
    level: 'B',
    email: 'epsilon11@scp-foundation.org',
    location: '机动部署',
    superior: 'O5-13',
    status: 1,
    avatar: 'https://cube.elemecdn.com/e/f5/3f28f2a7e22d5c5c3d7b9e0e7b3e9png.png',
    description: '九尾狐机动特遣队指挥官'
  },
  {
    id: 6,
    name: 'D-9341',
    level: 'D',
    email: 'd9341@scp-foundation.org',
    location: 'Site-19',
    superior: 'Dr. Bright',
    status: 1,
    avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png',
    description: 'D级人员，特殊测试对象'
  },
  {
    id: 7,
    name: 'Dr. Kondraki',
    level: 'A',
    email: 'kondraki@scp-foundation.org',
    location: 'Site-17',
    superior: 'O5-7',
    status: 1,
    avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png',
    description: '摄影与异常实体研究专家'
  },
  {
    id: 8,
    name: 'Dr. Glass',
    level: 'B',
    email: 'glass@scp-foundation.org',
    location: 'Site-64',
    superior: 'Dr. Kondraki',
    status: 1,
    avatar: 'https://cube.elemecdn.com/1/34/18c7e8f8e8f8e8f8e8f8e8f8e8f8e8.png',
    description: '收容措施优化专家'
  },
  {
    id: 9,
    name: 'O5-1',
    level: 'O5',
    email: 'o5-1@scp-foundation.org',
    location: 'Area-01',
    superior: '监督者议会',
    status: 1,
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    description: '监督者议会首席'
  },
  {
    id: 10,
    name: 'MTF Alpha-1',
    level: 'A',
    email: 'alpha1@scp-foundation.org',
    location: '机动部署',
    superior: 'O5议会',
    status: 1,
    avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
    description: '红右手指挥官'
  },
  {
    id: 11,
    name: 'Dr. Shaw',
    level: 'C',
    email: 'shaw@scp-foundation.org',
    location: 'Site-81',
    superior: 'Dr. Glass',
    status: 1,
    avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png',
    description: '初级研究员'
  },
  {
    id: 12,
    name: 'D-9983',
    level: 'D',
    email: 'd9983@scp-foundation.org',
    location: 'Site-19',
    superior: 'Dr. Bright',
    status: 0,
    avatar: 'https://cube.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6dpng.png',
    description: 'D级人员'
  },
  {
    id: 13,
    name: 'O5-7',
    level: 'O5',
    email: 'o5-7@scp-foundation.org',
    location: 'Area-07',
    superior: '监督者议会',
    status: 1,
    avatar: 'https://cube.elemecdn.com/e/f5/3f28f2a7e22d5c5c3d7b9e0e7b3e9png.png',
    description: '监督者议会成员'
  },
  {
    id: 14,
    name: 'Dr. Rights',
    level: 'B',
    email: 'rights@scp-foundation.org',
    location: 'Site-15',
    superior: 'O5-7',
    status: 1,
    avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png',
    description: '伦理委员会成员'
  },
  {
    id: 15,
    name: 'MTF Gamma-5',
    level: 'B',
    email: 'gamma5@scp-foundation.org',
    location: '机动部署',
    superior: 'O5-13',
    status: 1,
    avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png',
    description: '红鲱鱼机动特遣队指挥官'
  }
]);
*/

const tableData = ref()

// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0)

const catchData = (currentPage: number, pageSize: number) =>{
  getUserList(currentPage,pageSize).then((res)=>{
    if(res.code === 200){
      console.log("抓取数据")
      // originTableData.value = res.data
    }
  })
}

catchData(currentPage.value,pageSize.value)

// 搜索方法
const handleSearch = () => {
  currentPage.value = 1;
};

// 重置方法
const handleReset = () => {
  searchForm.name = '';
  searchForm.level = '';
  searchForm.location = '';
  searchForm.id = '';
  currentPage.value = 1;
};

// 每页条数改变
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
};

// 当前页改变
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
};

// 查看详情方法
const handleDetail = (row: any) => {
  console.log('查看详情：', row);
};

// 编辑方法
const handleEdit = (row: any) => {
  console.log('编辑：', row);
};

// 添加删除方法
const handleDelete = (row: any) => {
  console.log(row)
};
</script>

<style scoped>
.el-button + .el-button {
  margin-left: 8px;
}
</style>
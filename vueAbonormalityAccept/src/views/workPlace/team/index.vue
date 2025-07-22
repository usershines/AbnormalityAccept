<template>
  <el-card style="margin: -20px 0 0px;height: 750px;">
    <!-- 搜索表单区域 - 暗色主题 -->

    <template #header>
      <el-form
          :model="searchForm"
          inline
          class="search-form"
          style="display: flex;margin-bottom: -20px"
      >
        <el-form-item label="小队名称">
          <el-input
              v-model="searchForm.name"
              placeholder="请输入小队名称"
              clearable
              class="search-input"
              style="width: 130px;"
          ></el-input>
        </el-form-item>

        <el-form-item label="权限等级" class="search-item">
          <el-select
              v-model="searchForm.level"
              placeholder="请选择权限等级"
              clearable
              class="search-select"
              style="width: 150px;"
          >
            <el-option label="O5议会" value=5></el-option>
            <el-option label="A级" value=4></el-option>
            <el-option label="B级" value=3></el-option>
            <el-option label="C级" value=2></el-option>
            <el-option label="D级" value=1></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="状态" class="search-item">
          <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              class="search-select"
              style="width: 140px;"
          >
            <el-option label="空闲" value=0></el-option>
            <el-option label="任务中" value=1></el-option>
            <el-option label="无法活动" value=2></el-option>
            <el-option label="未知" value=3></el-option>
          </el-select>
        </el-form-item>

        <el-form-item style="margin-left: auto;margin-right: 10px;display: flex;flex-direction: row">
          <el-button
              type="primary"
              @click="handleSearch"
              class="search-button"
          >搜索
          </el-button>
          <el-button
              @click="handleReset"
              class="reset-button"
          >重置
          </el-button>
          <el-button
              @click="handleCreate"
              class="reset-button"
          >新建小队
          </el-button>
        </el-form-item>
      </el-form>
    </template>




    <!-- 卡片列表区域 - 收容单元风格 -->
    <template #default>
        <div style="margin: -10px 0 -30px; overflow-y: auto;overflow-x:hidden ; height: 630px">
            <el-row :gutter="20">
              <el-col
                  v-for="item in teamTableData"
                  :key="item.id"
                  :xs="24" :sm="12" :md="8" :lg="6"
                  style="width: 100%;margin-bottom: 10px"
              >
                <el-card
                    style="height: 300px;"
                    @click="handleDetail(item)"
                    shadow="hover"
                >
                  <template #header>
                    <div style="display: flex;flex-direction: row;">
                      <div>
                        <el-avatar :size="60" :src="teamAvatar" style="margin-bottom: 0px" />
                        <h3 style="margin-bottom: 5px;margin-top: 20px;overflow-x: hidden;white-space: nowrap">{{ item.name }}</h3>
                      </div>
                      <div style="display: flex;flex-direction: column;margin: 0px 0px 0 auto;align-items: flex-end">
                        <el-button
                            type="primary"
                            style="margin-bottom: 5px"
                            @click.stop="handleEdit(item)"
                        >
                          <i class="iconfont icon-edit"></i> 编辑
                        </el-button>
                        <el-button
                            type="danger"
                            @click.stop="handleDelete(item)"
                        >
                          <i class="iconfont icon-delete"></i> 删除
                        </el-button>
                      </div>
                    </div>

                    <div style="display: flex;flex-direction:row">
                      <el-tag
                          :type="getLevelTagType(item.level)"
                          style="margin: 2px 10px 0px 0"
                      >
                        {{ item.level }}级权限
                      </el-tag>
                      <el-tag :type="getStatusTagType(item.status)" class="status-tag">
                        {{ item.status }}
                      </el-tag>
                    </div>
                  </template>

                  <div style="margin-top: -10px">
                    <div class="info-item">
                      <span class="label">编号：{{ item.id }}</span>
                    </div>
                    <div class="info-item">

                    </div>
                    <div class="info-item">
                      <span class="label">成员：{{ selectedTeamMembers?.values.length }}人</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
        </div>
      <!-- 编辑小队弹窗 -->
      <el-dialog
          v-model="editDialogVisible"
          title="编辑小队信息"
          width="50%"
          class="containment-dialog"
      >
        <el-form
            :model="editTeamForm"
            ref="editFormRef"
            :rules="editFormRules"
            label-width="120px"
            label-position="left"
        >
          <el-form-item label="小队名称" prop="name">
            <el-input v-model="editTeamForm.name" placeholder="请输入小队名称" />
          </el-form-item>
          <el-form-item label="权限等级" prop="level">
            <el-select v-model="editTeamForm.level" placeholder="请选择权限等级">
              <el-option label="O5议会" value="5"></el-option>
              <el-option label="A级" value="4"></el-option>
              <el-option label="B级" value="3"></el-option>
              <el-option label="C级" value="2"></el-option>
              <el-option label="D级" value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="editTeamForm.status" placeholder="请选择状态">
              <el-option label="空闲" value="0"></el-option>
              <el-option label="任务中" value="1"></el-option>
              <el-option label="无法活动" value="2"></el-option>
              <el-option label="未知" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="正在执行任务ID" prop="resolvingQuestId">
            <el-input v-model="editTeamForm.resolvingQuestId" placeholder="请输入正在执行任务ID" />
          </el-form-item>
          <el-form-item label="队长ID" prop="leaderId">
            <el-input v-model="editTeamForm.leaderId" placeholder="请输入队长ID" />
          </el-form-item>
          <el-form-item label="简介" prop="description">
            <el-input v-model="editTeamForm.description" type="textarea" :rows="3" placeholder="请输入小队简介" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false" class="dialog-button">取消</el-button>
          <el-button type="primary" @click="handleEditSubmit" class="dialog-button">确认</el-button>
        </template>
      </el-dialog>
    </template>

    <!-- 分页组件区域 -->
    <template #footer>
      <div style="margin-top: -10px;display: flex;justify-content: flex-end">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[8, 16, 24]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            prev-text="上一页"
            next-text="下一页"
            class="containment-pagination"
        >
        </el-pagination>
      </div>
    </template>
  </el-card>

  <!-- 小队详情弹窗 - 收容单元报告风格 -->
  <el-dialog
    v-model="detailDialogVisible"
    :title="`${selectedTeam?.name} - 收容小队档案`"
    width="70%"
    class="containment-dialog"
    style="margin-top: 10px"
  >
    <div v-if="selectedTeam" class="team-detail">
      <div class="detail-header">
        <div class="security-stamp">
          <div class="stamp-content">
            <div class="stamp-title">SCP FOUNDATION</div>
            <div class="stamp-level">机密等级: {{ selectedTeam.level }}</div>
          </div>
        </div>
        <div class="header-content">
          <el-avatar :size="80" :src="teamAvatar" class="detail-avatar" />
          <div class="header-info">
            <h2 class="team-title">{{ selectedTeam.name }}</h2>
            <div class="info-row">
              <span><i class="iconfont icon-id"></i> 编号：{{ selectedTeam.id }}</span>
              <span><i class="iconfont icon-security"></i> 等级：
                <el-tag :type="getLevelTagType(selectedTeam.level)" class="clearance-tag">
                  {{ selectedTeam.level }}
                </el-tag>
              </span>
              <span><i class="iconfont icon-status"></i> 状态：
                <el-tag :type="getStatusTagType(selectedTeam.status)" class="status-tag">
                  {{ selectedTeam.status }}
                </el-tag>
              </span>
            </div>
            <div class="info-row">
              <span><i class="iconfont icon-superior"></i> 队长：{{ selectedTeam.leaderId }}</span>
            </div>
            <div class="info-row description">
              <span><i class="iconfont icon-description"></i> 简介：{{ selectedTeam.description }}</span>
            </div>
            <div class="info-row description">
              <span><i class="iconfont icon-description"></i> 正在执行任务：{{ selectedQuest?.questName }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <div style="display: flex;margin-top: -30px;align-items: center">
          <h3 class="section-title"><i class="iconfont icon-member"></i> 成员列表 ({{ selectedTeamMembers?.length.toString() }}人)</h3>
          <el-button style="margin-left: 50px" @click="handleAddMember">添加用户</el-button>
          <el-button style="margin-left: 50px" @click="handleDetail(selectedTeam)">刷新列表</el-button>
        </div>
        <el-table
          :data="selectedTeamMembers"
          border
          style="width: 100%"
          class="containment-table"
        >
          <el-table-column prop="username" label="姓名" width="180">
            <template #header>
              <span><i class="iconfont icon-name"></i> 姓名</span>
            </template>
          </el-table-column>
          <el-table-column prop="role" label="职位">
            <template #header>
              <span><i class="iconfont icon-role"></i> 职位</span>
            </template>
          </el-table-column>
          <el-table-column prop="level" label="权限等级">
            <template #header>
              <span><i class="iconfont icon-security"></i> 权限等级</span>
            </template>
            <template #default="scope">
              <el-tag :type="getLevelTagType(scope.row.level)" class="clearance-tag">
                {{ scope.row.level }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #header>
              <span><i class="iconfont icon-status"></i> 状态</span>
            </template>
            <template #default="scope">
              <el-tag :type="getMemberStatusTagType(scope.row.status)" class="status-tag">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column>
            <template #default="scope">
              <button @click="handleDeleteMember(scope.row.id)"> 移除</button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <template #footer>
      <el-button
        @click="detailDialogVisible = false"
        class="dialog-button"
      >
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>

  <!-- 成员详情弹窗 -->
  <el-dialog
    v-model="memberDialogVisible"
    :title="`人员档案`"
    width="30%"
    class="containment-dialog"
  >
    <div style="display: flex">
      <el-input v-model="memberAddId" placeholder="在这里输入用户ID"/>
      <el-button @click="handleAddMemberWith(memberAddId)">添加为成员</el-button>
    </div>
    <el-table :data="memberList" stripe style="width: 100%">
      <el-table-column prop="username" label="名称" width="180" />
      <el-table-column prop="level" label="等级" width="180" />
      <el-table-column label="操作" >
        <template #default="scope">
          <el-button @click=handleAddMemberWith(scope.row.id)>添加</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="memberPageNum"
        layout="total, sizes, prev, pager, next, jumper"
        :total="memberPageTotal"
        @current-change="handleCurrentMemberChange"
    />

    <template #footer>
      <el-button
        @click="memberDialogVisible = false"
        class="dialog-button"
      >
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>


<!--新建小队弹窗-->
  <el-dialog
      v-model="createTeamDialogVisible"
      title="新建小队"
      width="50%"
      class="containment-dialog"
  >
    <el-form
        :model="createForm"
        ref="createFormRef"
        label-width="100px"
    >
      <el-form-item label="小队名称">
        <el-input v-model="createForm.name"></el-input>
      </el-form-item>
      <el-form-item label="小队等级">
        <el-select v-model="createForm.level">
          <el-option label="1级" value="1"></el-option>
          <el-option label="2级" value="2"></el-option>
          <el-option label="3级" value="3"></el-option>
          <el-option label="4级" value="4"></el-option>
          <el-option label="5级" value="5"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="队长ID">
        <el-input v-model="createForm.leaderId"></el-input>
      </el-form-item>
      <el-form-item label="小队简介">
        <el-input v-model="createForm.description"></el-input>
      </el-form-item>

    </el-form>
    <template #footer>
      <el-button @click="createTeamDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveCreate">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import * as TeamApi from '@/api/team';
import * as QuestApi from '@/api/quest';
import * as UserApi from '@/api/user';
import type { Team, TeamRequest, TeamUpdateRequest } from '@/api/team';
import type { Quest } from '@/api/quest';
import type { User } from '@/api/user';
import {ElMessage, type FormInstance} from 'element-plus';
import * as facilityApi from "@/api/facility.ts";

const userAvatar = ref("/src/assets/pic/user.png");
// 获取数据
const catchTeamTableData = () => {
  TeamApi.getTeamList(pageNum.value, pageSize.value).then((res) => {
    if (res.code === 200) {
      teamTableData.value = res.data.list;
      total.value = res.data.total;
    } else if (res.code === 501) {
      ElMessage.error('权限不足，无法获取小队信息');
    } else {
      ElMessage.error('小队信息获取失败：' + res.msg);
    }
  }).catch((err) => {
    console.log(err);
    ElMessage.error('小队信息获取失败：' + err.msg);
  });
};

// 队伍表格数据
const teamTableData = ref<Team[]>([]);
const teamAvatar = ref("/src/assets/pic/team.png");
const createTeamDialogVisible = ref(false);
// 分页相关数据
const pageNum = ref(1);
const pageSize = ref(8);
const total = ref(0);

// 弹窗相关
const detailDialogVisible = ref(false);
const memberDialogVisible = ref(false);
const selectedTeam = ref<Team | null>(null);
const selectedTeamMembers = ref<User[]>();
const selectedMember = ref<User | null>(null);
const selectedQuest = ref<Quest | null>(null);
const createFormRef = ref();
const createForm = ref<TeamRequest>({
  name: '',

  level: 0,

  leaderId: 0,

  description:''

});

// 初始化页面
onMounted(() => {
  catchTeamTableData();
});

// 搜索方法
const searchForm = ref({
  id: null,
  name: '',
  status: null,
  resolvingQuestId: null,
  resolvingQuestName: null,
  level: null,
  description: '',
  leaderId: null,
  minLevel: 1,
  maxLevel: 5,
  isActive: 1,
  stateList: null,
  pageNum: pageNum.value,
  pageSize: pageSize.value,
});

const handleSearch = () => {
  console.log('搜索表单',searchForm.value);
  // 确保分页参数正确
  searchForm.value.pageNum = pageNum.value;
  searchForm.value.pageSize = pageSize.value;

  console.log(searchForm.value);
  TeamApi.findTeamByCondition(searchForm.value).then((res) => {
    if (res.code === 200) {
      teamTableData.value = res.data.list;
      total.value = res.data.total;
    } else if (res.code === 501) {
      ElMessage.error('权限不足，无法查询队伍');
    } else {
      ElMessage.error('查询队伍失败：' + res.msg);
    }
  }).catch((err) => {
    ElMessage.error('查询队伍失败：' + err.msg);
  });
};

// 获取权限等级对应的标签类型
const getLevelTagType = (level: number) => {
  const types: Record<string, string> = {
    5: 'danger',
    4: 'warning',
    3: 'primary',
    2: 'success',
    1: 'info',
  };
  return types[level] || '';
};

// 获取小队状态对应的标签类型
const getStatusTagType = (status: number) => {
  const types: Record<string, string> = {
    0: 'success',
    1: 'warning',
    2: 'danger',
    3: 'info',
  };
  return types[status] || '';
};

// 获取成员状态对应的标签类型
const getMemberStatusTagType = (status: string) => {
  const types: Record<string, string> = {
    '在岗': 'success',
    '任务中': 'warning',
    '医疗休假': 'danger',
    '休假': 'info',
  };
  return types[status] || '';
};

//新建小队
const handleCreate = () => {
  createTeamDialogVisible.value = true;};
// 重置表单
const saveCreate = () => {
  if (!createFormRef.value) return;
  createFormRef.value.validate((valid) => {
    if (valid) {
      TeamApi.addTeam(createForm.value).then((response) => {
        if (response.code === 200) {
          console.log("新建小队成功:", createForm.value);
          ElMessage.success('新建小队成功');
          createTeamDialogVisible.value = false;
          catchTeamTableData()
          createFormRef.value.resetFields();
        }
      }).catch((error) => {
        console.log('服务器错误',error);
        ElMessage.error('服务器错误')
      })
    }
  })
}


// 重置方法
const handleReset = () => {
  searchForm.value = {
    id: null,
    name: '',
    status: null,
    resolvingQuestId: null,
    resolvingQuestName: null,
    level: null,
    description: '',
    leaderId: null,
    minLevel: 1,
    maxLevel: 5,
    isActive: 1,
    stateList: null,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
  };
  catchTeamTableData();
};

// 每页条数改变
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  catchTeamTableData(); // 使用搜索方法重新加载数据
};

// 当前页改变
const handleCurrentChange = (val: number) => {
  pageNum.value = val;
  catchTeamTableData(); // 使用搜索方法重新加载数据
};

// 查看详情方法
const handleDetail = async (row: Team) => {
  selectedTeam.value = row;
  detailDialogVisible.value = true;
  selectedTeamMembers.value = []; // 清空成员列表
  
  const teamId = row.id;
    try {
      const res = await TeamApi.getMemberList(teamId);
      console.log(res);
      if (res.code === 200 && res.data != null) {
        selectedTeamMembers.value = res.data;
      } else if (res.code === 501) {
        ElMessage.error(`权限不足，无法获取成员ID: ${teamId} 的信息`);
      } else {
        ElMessage.error(`获取成员ID: ${teamId} 的信息失败：${res.msg}`);
      }
    } catch (err) {
      ElMessage.error(`获取成员ID: ${teamId} 的信息失败：${(err as Error).message}`);
    }

  // 获取小队正在执行的任务信息
  if (row.resolvingQuestId) {
    try {
      const res = await QuestApi.getQuest(row.resolvingQuestId);
      if (res.code === 200) {
        selectedQuest.value = res.data;
      } else if (res.code === 501) {
        ElMessage.error('权限不足，无法获取任务信息');
      } else {
        ElMessage.error('获取任务信息失败：' + res.msg);
      }
    } catch (err) {
      ElMessage.error('获取任务信息失败：' + (err as Error).message);
    }
  }
};
// 编辑弹窗相关
const editDialogVisible = ref(false);
const editTeamForm = ref<TeamUpdateRequest>({
  id: 0,
  name: '',
  status: 0,
  resolvingQuestId:null,
  level: 0,
  description: '',
  leaderId: null,
});
const editFormRules = {
  name: [{ required: true, message: '请输入小队名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择权限等级', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
};

const handleEdit = (row: Team) => {
  editTeamForm.value = {
    id: row.id,
    name: row.name,
    status: row.status,
    resolvingQuestId: row.resolvingQuestId,
    level: row.level,
    description: row.description,
    leaderId: row.leaderId,
  };
  editDialogVisible.value = true;
};
// 编辑方法
// const handleEdit = (row: Team) => {
//   ElMessage.info(`编辑小队: ${row.name}`);
//
//   // 这里可以添加实际编辑逻辑
// };
const handleEditSubmit = async () => {
  try {
    const res = await TeamApi.updateTeam(editTeamForm.value);
    if (res.code === 200) {
      ElMessage.success('小队信息编辑成功');
      editDialogVisible.value = false;
      catchTeamTableData(); // 重新获取小队列表，更新表格数据
    } else {
      ElMessage.error('编辑失败：' + res.msg);
    }
  } catch (err) {
    ElMessage.error('编辑失败：' + (err as Error).message);
  }
};
const editFormRef = ref<FormInstance | null>(null);
// 删除方法
const handleDelete = (row: Team) => {
  TeamApi.deleteTeam(row.id).then((res) => {
    if (res.code === 200) {
      ElMessage.success(`小队 ${row.name} 删除成功`);
      // 重新加载数据
      catchTeamTableData();
    } else if (res.code === 501) {
      ElMessage.error('权限不足，无法删除小队');
    } else {
      ElMessage.error('删除小队失败：' + res.msg);
    }
  }).catch((err) => {
    ElMessage.error('删除小队失败：' + (err as Error).message);
  });
}

// 添加小队成员
const memberPageNum = ref(1)
const memberPageSize = ref(10)
const memberPageTotal = ref(0)
const memberList = ref<User[]>()
const memberAddId = ref<number | null>(null)
const handleCurrentMemberChange = (val: number) => {
  memberPageNum.value = val;
  catchMemberNoTeam()
}

const catchMemberNoTeam = () =>{
  TeamApi.findUserBelongNoTeam(memberPageNum.value,memberPageSize.value).then(res=>{
    console.log(res)
    if (res.code === 200) {
      memberList.value = res.data.list;
      memberPageTotal.value = res.data.total;
      ElMessage.success('获取到当前可加入小队的成员')
    }else {
      ElMessage.error('获取无小队成员错误'+res.msg);
    }
  }).catch((err) => {
    console.log(err)
    ElMessage.error('获取无小队成员错误'+err.msg);
  })
}

const handleAddMember = () =>{
  memberDialogVisible.value = true
  catchMemberNoTeam()
}

const handleAddMemberWith = (userId: number | null) => {
  const teamId = selectedTeam.value?.id
  if( userId === null  ){
    ElMessage('用户id为空！')
    return;
  }
  console.log('向队伍',teamId,'中添加用户',userId)
  if(teamId === null){
    ElMessage.error('无法获取队伍id')
    return
  }else {
    TeamApi.addMember(teamId, userId).then(res=>{
      console.log(res)
      if (res.code === 200) {
        ElMessage.success('用户添加成功，请刷新页面')
      }
    }).catch((err) => {
      console.log(err)
      ElMessage.error('添加该用户失败:'+err.msg)
    })
  }

}

const handleDeleteMember = (userId: number) => {
  if (userId === null || selectedTeam.value?.id === null) {
    ElMessage.error('用户不存在')
    return
  }
  ElMessageBox.confirm(
      'proxy will permanently delete the file. Continue?',
      'Warning',
      {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning',
      }
  )
      .then(() => {
        TeamApi.removeMember(selectedTeam.value.id,userId).then(res=>{
          if (res.code === 200) {
            ElMessage({
              type: 'success',
              message: '删除成功，请刷新页面',
            })
          }
        }).catch((err) => {
          console.log(err)
          ElMessage.error('删除服务器错误：'+err.msg)
        })
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: 'Delete canceled',
        })
      })
}
//1
</script>
<style scoped>
/* 弹窗样式增强 */
.containment-dialog {
  background: linear-gradient(135deg, #0c1a33, #1a2a4a);
  border: 1px solid #4a6fb3;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(74, 111, 179, 0.5);
}


/* 详情页样式增强 */
.team-detail {
  position: relative;
  background: rgba(15, 30, 61, 0.85);
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #304878;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.security-stamp {
  position: absolute;
  top: -15px;
  right: -15px;
  width: 120px;
  height: 120px;
  border: 4px solid #e63946;
  border-radius: 50%;
  background: rgba(230, 57, 70, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  transform: rotate(15deg);
  z-index: 10;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(230, 57, 70, 0.3);
}

.security-stamp.small {
  width: 100px;
  height: 100px;
  top: -10px;
  right: -10px;
}

.stamp-content {
  text-align: center;
  transform: rotate(-15deg);
}

.stamp-title {
  font-size: 12px;
  font-weight: bold;
  color: #ff6b7b;
  text-transform: uppercase;
  letter-spacing: 1px;
  text-shadow: 0 1px 2px rgba(0,0,0,0.5);
}

.stamp-level {
  font-size: 10px;
  color: #ff9aa2;
  margin-top: 3px;
  text-shadow: 0 1px 1px rgba(0,0,0,0.3);
}

.detail-header {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(74, 111, 179, 0.3);
}

.detail-avatar {
  border: 3px solid #4a6fb3;
  background-color: #0a1429;
  margin-right: 20px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.3);
}

.team-title {
  margin-top: 0;
  margin-bottom: 15px;
  color: #ffffff;
  font-size: 26px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
  border-bottom: 2px solid #4a6fb3;
  background: linear-gradient(to right, rgba(74,111,179,0.2), transparent);
  padding: 12px 15px;
  border-radius: 4px;
  position: relative;
}

.team-title::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(to right, #4a6fb3, transparent);
}

.info-row {
  display: flex;
  margin-bottom: 15px;
  font-size: 15px;
  flex-wrap: wrap;
}

.info-row span {
  margin-right: 30px;
  margin-bottom: 12px;
  min-width: 45%;
  display: flex;
  align-items: center;
  background: rgba(10, 20, 41, 0.3);
  padding: 8px 12px;
  border-radius: 4px;
  border-left: 3px solid #4a6fb3;
}

.info-row i {
  margin-right: 10px;
  color: #a0b9e0;
  font-size: 16px;
  min-width: 20px;
  text-align: center;
}

.info-row.mission {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #4a6fb3;
  color: #f9a03f;
  font-weight: bold;
  background: transparent;
  border-left: none;
}

.info-row.description {
  margin-top: 20px;
  border-top: 1px dashed #4a6fb3;
  color: #f0f7ff;
  width: 100%;
  background: rgba(10, 20, 41, 0.4);
  padding: 15px;
  border-radius: 4px;
  border-left: 3px solid #4a6fb3;
  box-shadow: inset 0 0 10px rgba(0,0,0,0.2);
}

.detail-section {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(74, 111, 179, 0.3);
}

.section-title {
  color: #ffffff;
  font-size: 20px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(74, 111, 179, 0.3);
}

.section-title i {
  margin-right: 10px;
  color: #4a9cf0;
  font-size: 22px;
}

/* 表格样式增强 */
.containment-table {
  background: rgba(15, 30, 61, 0.7);
  border: 1px solid #304878;
  border-radius: 8px;
  overflow: hidden;
}

.containment-table :deep(th) {
  background: linear-gradient(to bottom, #1a2a4a, #0c1a33) !important;
  color: #ffffff;
  font-weight: bold;
  border-bottom: 1px solid #304878;
  font-size: 15px;
}

.containment-table :deep(td) {
  background: rgba(10, 20, 41, 0.5);
  color: #ffffff;
  border-bottom: 1px solid #304878;
  font-size: 14px;
}

.containment-table :deep(.el-table__row:hover td) {
  background-color: rgba(74, 111, 179, 0.3) !important;
}

/* 任务时间线样式 */
.mission-timeline {
  padding-left: 20px;
}

.mission-card {
  background: rgba(15, 30, 61, 0.7);
  border: 1px solid #304878;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s ease;
}

.mission-card:hover {
  border-color: #4a6fb3;
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(74, 111, 179, 0.3);
}

.mission-title {
  color: #ffffff;
  margin-top: 0;
  margin-bottom: 10px;
  font-size: 16px;
  border-bottom: 1px solid #304878;
  padding-bottom: 8px;
}

.mission-description {
  color: #e0f0ff;
  line-height: 1.7;
  margin-bottom: 10px;
}

.mission-status {
  display: flex;
  align-items: center;
  color: #a0b9e0;
  font-size: 14px;
}

.mission-duration {
  margin-left: 20px;
  display: flex;
  align-items: center;
}

.mission-duration i {
  margin-right: 5px;
}

/* 成员详情样式 */
.member-detail {
  position: relative;
  background: rgba(15, 30, 61, 0.85);
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #304878;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.member-header {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(74, 111, 179, 0.3);
}

.member-avatar {
  border: 3px solid #4a6fb3;
  background-color: #0a1429;
  margin-right: 20px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.3);
}

.member-name {
  margin-top: 0;
  margin-bottom: 15px;
  color: #ffffff;
  font-size: 24px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.5);
  border-bottom: 2px solid #4a6fb3;
  background: linear-gradient(to right, rgba(74,111,179,0.2), transparent);
  padding: 12px 15px;
  border-radius: 4px;
  position: relative;
}

.member-name::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(to right, #4a6fb3, transparent);
}

.skills {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10px;
}

.skill-tag {
  background: rgba(74, 111, 179, 0.3);
  color: #ffffff;
  border: 1px solid #4a6fb3;
  border-radius: 12px;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  margin: 0 8px 8px 0;
  font-size: 14px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.skill-tag:hover {
  background: rgba(74, 111, 179, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 111, 179, 0.3);
}

/* 按钮样式 */
.dialog-button {
  background: linear-gradient(to right, #4a6fb3, #3a5a9c);
  border: none;
  color: white;
  font-weight: bold;
  transition: all 0.3s ease;
  padding: 10px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.2);
}

.dialog-button:hover {
  background: linear-gradient(to right, #5a7fc3, #4a6aac);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(74, 111, 179, 0.4);
}

/* 标签样式增强 */
.clearance-tag {
  font-weight: bold;
  border-radius: 12px;
  padding: 0 12px;
  height: 26px;
  line-height: 26px;
  border: none;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  text-shadow: 0 1px 1px rgba(0,0,0,0.3);
}

.status-tag {
  font-weight: bold;
  border-radius: 12px;
  padding: 0 12px;
  height: 26px;
  line-height: 26px;
  border: none;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  text-shadow: 0 1px 1px rgba(0,0,0,0.3);
}

.value {
  white-space: nowrap; /* 不换行 */
  overflow: hidden; /* 内容溢出隐藏 */
  text-overflow: ellipsis; /* 使用省略号表示内容溢出 */
}
.create-button {
  background-color: #28a745;
  border-color: #28a745;
}

.create-button:hover {
  background-color: #218838;
  border-color: #218838;
}

</style>
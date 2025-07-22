<template>
  <el-card style="margin-top: -20px; height: 750px">
    <!-- 搜索表单区域 - 暗色主题 -->
    <template #header>
      <el-form
          :model="searchForm"
          inline
          style="margin-bottom: -25px;"
      >
        <el-form-item label="任务名称" class="search-item">
          <el-input
              v-model="searchForm.questName"
              placeholder="请输入任务名称"
              clearable
              class="search-input"
              style="width: 125px;"
          ></el-input>
        </el-form-item>


        <el-form-item label="任务代号" class="search-item">
          <el-input
              v-model="searchForm.questCode"
              placeholder="请输入任务代号"
              clearable
              class="search-input"
              style="width: 125px;"
          ></el-input>
        </el-form-item>

        <el-form-item label="任务等级" class="search-item">
          <el-select
              v-model="searchForm.questLevel"
              placeholder="请选择"
              clearable
              class="search-select"
              style="width: 125px;"
          >
            <el-option
                v-for="(label, value) in levelMap"
                :key="value"
                :label="label"
                :value="Number(value)"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="任务状态" class="search-item">
          <el-select
              v-model="searchForm.state"
              placeholder="请选择"
              clearable
              class="search-select"
              style="width: 125px;"
          >
            <el-option
                v-for="(label, value) in statusMap"
                :key="value"
                :label="label"
                :value="Number(value)"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              @click="handleSearch"
              class="search-button"
          >
            <i class="iconfont icon-search"></i> 搜索
          </el-button>
          <el-button
              @click="handleReset"
              class="reset-button"
          >
            <i class="iconfont icon-reset"></i> 重置
          </el-button>
          <el-button
              type="primary"
              @click="openCreateDialog"
              style="border-radius: 8px; padding: 8px 20px;"
          >
            <i class="iconfont icon-plus"></i> 新建任务
          </el-button>
        </el-form-item>
      </el-form>
    </template>

    <template #default>
      <!-- 卡片列表区域 - 任务单元风格 -->
      <div class="quest-cards">
        <el-row :gutter="10">
          <el-col
              v-for="item in originTableData"
              :key="item.id"
              :span="6"
              class="card-col"
          >
            <el-card
                class="containment-card"
                @click="handleDetail(item)"
            >
              <!-- 卡片头部：头像 + 标题 + 等级标签 -->
              <div class="card-header">
                <div class="avatar-container">
                  <div class="security-badge">
                    <i class="iconfont icon-lock"></i>
                  </div>
                  <el-avatar
                      :size="64"
                      src="selectedQuest.avatar"
                      class="quest-avatar"
                  />
                </div>
                <div class="info-item">
                  <span class="label"><i class="iconfont icon-questCode"></i> 任务代号：</span>
                  <span class="value">{{ item.questCode }}</span>
                </div>
              </div>

              <!-- 卡片内容：代号、状态、截止日期、负责人 -->
              <div class="card-content">
                <div class="info-item">
                  <span class="label"><i class="iconfont icon-questName"></i> 任务名称：</span>
                  <span class="value">{{ item.questName }}</span>
                </div>
                <div class="info-item">
                  <span class="label"><i class="iconfont icon-id"></i> 序号：</span>
                  <span class="value">{{ item.id }}</span>
                </div>
                <div class="info-item">
                  <span class="label"><i class="iconfont icon-level"></i> 等级：</span>
                  <el-tag :type="getLevelTagType(item.questLevel)" class="level-tag">
                    {{ levelMap[item.questLevel] || '未知' }}
                  </el-tag>
                </div>
                <div class="info-item">
                  <span class="label"><i class="iconfont icon-status"></i> 状态：</span>
                  <el-tag :type="getStatusTagType(item.state)" class="status-tag">
                    {{ statusMap[item.state] || '未知' }}
                  </el-tag>
                </div>

                <div class="info-item">
                  <span class="label"><i class="iconfont icon-resolvingByTeamName"></i> 执行小队：</span>
                  <span class="value">{{ item.resolvingByTeamName }}</span>
                </div>
                <!--            <div class="info-item">-->
                <!--              <span class="label"><i class="iconfont icon-description"></i> 描述：</span>-->
                <!--              <span class="value">{{ item.questDescription }}</span>-->
                <!--            </div>-->
              </div>

              <!-- 操作按钮 -->
              <div class="card-actions">
                <el-button
                    type="text"
                    size="small"
                    @click.stop="handleEdit(item)"
                    class="edit-btn"
                >
                  <i class="iconfont icon-edit"></i> 编辑
                </el-button>
                <el-button
                    type="text"
                    size="small"
                    class="delete-btn"
                    @click.stop="handleDelete(item)"
                >
                  <i class="iconfont icon-delete"></i> 删除
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </template>


    <!-- 分页组件区域 -->
    <template #footer>
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[8, 16, 24]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          prev-text="上一页"
          next-text="下一页"
          class="containment-pagination"
      >
      </el-pagination>
    </template>
  </el-card>


  <!-- 任务详情弹窗 - 任务单元报告风格 -->
  <el-dialog
      v-model="detailDialogVisible"
      :title="`${selectedQuest?.questName} - 任务档案`"
      width="70%"
      class="containment-dialog"
  >
    <div v-if="selectedQuest" class="quest-detail">
      <div class="detail-header">
        <div class="security-stamp">
          <div class="stamp-content">
            <div class="stamp-title">QUEST MANAGEMENT</div>
            <div class="stamp-level">机密等级: {{ levelMap[selectedQuest.questLevel] || '未知' }}</div>
          </div>
        </div>
        <div class="header-content">
          <el-avatar :size="80" :src="selectedQuest.avatar" class="detail-avatar" />
          <div class="header-info">
            <h2 class="quest-title">{{ selectedQuest.questName }}</h2>
            <div class="info-row">
              <span>任务代号：{{ selectedQuest.questCode }}</span>
              <span style="margin-top: 5px">等级：
              <el-tag :type="getLevelTagType(selectedQuest.questLevel)" class="level-tag">
                {{ levelMap[selectedQuest.questLevel] || '未知' }}
              </el-tag>
            </span>
              <span style="margin-top: 5px">状态：
              <el-tag :type="getStatusTagType(selectedQuest.state)" class="state-tag">
                {{ statusMap[selectedQuest.state] || '未知' }}
              </el-tag>
            </span>
            </div>
            <div class="info-row">
              <span><i class="iconfont icon-assignee"></i> 执行小队：{{ selectedQuest.resolvingByTeamName }}</span>
            </div>
            <div class="info-row description">
              <span><i class="iconfont icon-description"></i> 描述：{{ selectedQuest.questDescription }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h3 class="section-title"><i class="iconfont icon-history"></i> 任务历史</h3>
        <el-timeline class="mission-timeline">
          <el-timeline-item
              v-for="(history, index) in selectedQuest.history"
              :key="index"
              :timestamp="history.date"
              placement="top"
          >
            <el-card class="mission-card">
              <h4 class="mission-title">{{ history.action }}</h4>
              <p class="mission-description">{{ history.comment }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
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

  <!-- 新建任务弹窗 -->
  <el-dialog
      v-model="createDialogVisible"
      title="新建任务"
      width="60%"
      class="containment-dialog"
  >
    <el-form
        :model="createForm"
        ref="createFormRef"
        :rules="formRules"
        label-width="120px"
        class="quest-form"
    >
      <el-form-item label="任务代号" prop="questCode">
        <el-input v-model="createForm.questCode" placeholder="请输入任务代号"></el-input>
      </el-form-item>
      <el-form-item label="任务名称" prop="questName">
        <el-input v-model="createForm.questName" placeholder="请输入任务名称"></el-input>
      </el-form-item>

      <el-form-item label="任务等级" prop="questLevel">
        <el-radio-group v-model="createForm.questLevel">
          <el-radio :label=1>D级</el-radio>
          <el-radio :label=2>C级</el-radio>
          <el-radio :label=3>B级</el-radio>
          <el-radio :label=4>A级</el-radio>
          <el-radio :label=5>S级</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="任务状态" prop="state">
        <el-radio-group v-model="createForm.state">
          <el-radio :label=0>未开始</el-radio>
          <el-radio :label=1>进行中</el-radio>
          <el-radio :label=2>已完成</el-radio>
          <el-radio :label=3>已取消</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="执行小队" prop="resolvingByTeamName">
        <el-button plain @click="handleSelectTeam(true)">
          选择小队
        </el-button>
        <el-input v-model="createForm.resolvingByTeamName" placeholder="请输入小队名称" />
      </el-form-item>

      <el-form-item label="任务描述" prop="description">
        <el-input
            type="textarea"
            v-model="createForm.questDescription"
            placeholder="请输入任务描述"
            :rows="4"
        ></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button
          @click="createDialogVisible = false"
          class="dialog-button"
      >
        <i class="iconfont icon-cancel"></i> 取消
      </el-button>
      <el-button
          type="primary"
          @click="handleCreateSubmit"
          class="dialog-button"
      >
        <i class="iconfont icon-save"></i> 保存
      </el-button>
    </template>
  </el-dialog>


  <!-- 编辑任务弹窗 -->
  <el-dialog
      v-model="editDialogVisible"
      title="编辑任务"
      width="60%"
      class="containment-dialog"
  >
    <el-form
        :model="editForm"
        ref="editFormRef"
        :rules="formRules"
        label-width="120px"
        class="quest-form"
    >
      <el-form-item label="任务代号" prop="questCode">
        <el-input v-model="editForm.questCode" placeholder="请输入任务代号" disabled></el-input>
      </el-form-item>
      <el-form-item label="任务名称" prop="questName">
        <el-input v-model="editForm.questName" placeholder="请输入任务名称"></el-input>
      </el-form-item>

      <el-form-item label="任务等级" prop="questLevel">
        <el-radio-group v-model="editForm.questLevel">
          <el-radio :label="1">D级</el-radio>
          <el-radio :label="2">C级</el-radio>
          <el-radio :label="3">B级</el-radio>
          <el-radio :label="4">A级</el-radio>
          <el-radio :label="5">S级</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="任务状态" prop="state">
        <el-radio-group v-model="editForm.state">
          <el-radio :label="0">未开始</el-radio>
          <el-radio :label="1">进行中</el-radio>
          <el-radio :label="2">已完成</el-radio>
          <el-radio :label="3">已取消</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="执行小队" prop="resolvingByTeamName">
        <el-button plain @click="handleSelectTeam(false)">
          选择小队
        </el-button>
        <el-input v-model="editForm.resolvingByTeamName" placeholder="请输入执行小队"></el-input>
      </el-form-item>

      <el-form-item label="任务描述" prop="description">
        <el-input
            type="textarea"
            v-model="editForm.questDescription"
            placeholder="请输入任务描述"
            :rows="4"
        ></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button
          @click="editDialogVisible = false"
          class="dialog-button"
      >
        <i class="iconfont icon-cancel"></i> 取消
      </el-button>
      <el-button
          type="primary"
          @click="handleEditSubmit"
          class="dialog-button"
      >
        <i class="iconfont icon-save"></i> 保存
      </el-button>
    </template>
  </el-dialog>

<!--  选择小队-->
  <el-dialog
      v-model="dialogCreateTeamVisible"
      title="Tips"
      width="500"
      draggable
      overflow
  >
    <span>空闲小队</span>
    <div style="display: flex">
      <el-input v-model="requestTeam" placeholder="在这里输入小队名称"/>
      <el-button @click="handleAddAsRequestTeam(requestTeam)">选择该小队</el-button>
    </div>
    <el-table :data="teamList" stripe style="width: 100%">
      <el-table-column prop="name" label="名称" width="180" />
      <el-table-column prop="level" label="等级" width="180" />
      <el-table-column label="操作" >
        <template #default="scope">
          <el-button @click=handleAddAsRequestTeam(scope.row.name)>添加</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
        v-model:current-page="teamPageNum"
        layout="total, prev, pager, next, jumper"
        :total="teamPageTotal"
        @current-change="handleCurrentTeamChange"
    />
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogCreateTeamVisible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted, nextTick } from 'vue';
import {Tooltip} from 'element-ui'
import { ElMessage, ElMessageBox } from 'element-plus';
import * as QuestApi from "@/api/quest.ts"


// 状态映射关系
const statusMap: Record<number, string> = {
  0: '未开始',
  1: '进行中',
  2: '已完成',
  3: '已取消'
};


const statusReverseMap: Record<string, number> = {
  '未开始': 0,
  '进行中': 1,
  '已完成': 2,
  '已取消': 3
};

// 等级映射关系 (1-5 对应 D-S)
const levelMap: Record<number, string> = {
  1: 'D级',
  2: 'C级',
  3: 'B级',
  4: 'A级',
  5: 'S级'
};

const levelReverseMap: Record<string, number> = {
  'D级': 1,
  'C级': 2,
  'B级': 3,
  'A级': 4,
  'S级': 5
};

// 状态标签类型
const getStatusTagType = (state: number) => {
  const statusText = statusMap[state] || '未知';
  const types: Record<string, string> = {
    '未开始': 'info',
    '进行中': 'warning',
    '已完成': 'success',
    '已取消': 'danger'
  };
  return types[statusText] || '';
};
// 获取等级标签类型
const getLevelTagType = (level: number) => {
  const types: Record<number, string> = {
    1: 'info',      // D级
    2: 'success',   // C级
    3: 'primary',   // B级
    4: 'warning',   // A级
    5: 'danger'     // S级
  };
  return types[level] || '';
};
// 定义任务历史接口
interface QuestHistory {
  date: string;
  action: string;
  comment: string;
}

// 定义任务接口
interface Quest {
  id: number;
  questCode: string;
  questName: string;
  questLevel: number; // 添加任务等级字段
  questDescription: string;
  state: number;
  resolvingByTeamId: number;
  resolvingByTeamName: string;
  deadline?: string;
  assignee?: string;
  avatar?: string;
  progress?: number;
  history?: any[];
}

interface QuestParam {
  id: number | null,
  questCode : string | null,
  questName: string | null,
  questLevel: number | null,
  resolvingByTeamId: number | null,
  resolvingByTeamName: string | null,
  questDescription: string | null,
  state: number | null,

  //多值查询
  stateList: number[] | null,

  //范围查询参数
  minQuestLevel: number | null;
  maxQuestLevel: number | null;

  pageNum : number | null;
  pageSize: number | null;
}


interface QuestRequest {
  questCode: string;
  questName : string;
  questLevel: number;
  resolvingByTeamId: number;
  resolvingByTeamName: string;
  questDescription: string;
  state: number;

}

// 搜索表单数据
const searchForm = ref<QuestParam>({
  id: null,
  questCode : null,
  questName : null ,
  questLevel: null,
  resolvingByTeamId: null,
  resolvingByTeamName: null,
  questDescription: null,
  state : null,
  stateList:null,
  minQuestLevel: null,
  maxQuestLevel: null,
  pageNum: null,
  pageSize : null,
});

// 原始表格数据
const originTableData = ref<Quest[]>([]);

// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(8);
const total = ref(0);

// 弹窗相关
const detailDialogVisible = ref(false);
const selectedQuest = ref<Quest | null>(null);

// 新建任务弹窗相关
const createDialogVisible = ref(false);
const createFormRef = ref();
const createForm = ref<QuestRequest>({
  questCode: '',
  questName: '',
  questLevel: 3,
  resolvingByTeamId: 0,
  resolvingByTeamName: '',
  questDescription: '',
  state: 0,
});

// 编辑任务弹窗相关
const editDialogVisible = ref(false);
const editFormRef = ref();
const editForm = ref<Quest>({
  id: 0,
  questCode: '',
  questName: '',
  questLevel: 1,
  questDescription: '',
  state: 0,
  resolvingByTeamId: 0,
  resolvingByTeamName: '',
});


// 表单验证规则
const formRules = reactive({
  questCode:[
    {required: true, message: '请输入任务代号', trigger: 'blur' }
  ],
  questName: [
    { required: true, message: '请输入任务名称', trigger: 'blur' },
    { min: 2, max: 50, message: '任务名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择任务等级', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择任务状态', trigger: 'change' }
  ],
  resolvingByTeamId: [
    { required: true, message: '请输入执行小队', trigger: 'blur' },
  ],

});

// 初始化数据
onMounted(() => {
  fetchData();
});

// 初始化数据函数
const fetchData = async () => {
  try {
    const response = await QuestApi.getQuestList(currentPage.value, pageSize.value);
    if (response.code === 200) {
      originTableData.value = response.data.list;
      total.value = response.data.total;
      ElMessage.success('数据加载成功');
    } else {
      ElMessage.error('获取数据失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('获取数据失败');
    console.error('获取数据失败:', error);
  }
};


// 搜索方法
const handleSearch = async () => {
  searchForm.value.pageNum = currentPage.value;
  searchForm.value.pageSize = pageSize.value;
  try {
    const questParam = {
      questName: searchForm.value.questName ,
      questCode: searchForm.value.questCode,
      questLevel: searchForm.value.questLevel,
      state: searchForm.value.state,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    };

    const response = await QuestApi.searchQuests(questParam);
    if (response.code === 200) {
      originTableData.value = response.data.list;
      total.value = response.data.total;
      ElMessage.success('搜索成功');
    } else {
      ElMessage.error('搜索失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('搜索失败');
    console.error('搜索失败:', error);
  }
};
// 重置方法
const handleReset = () => {
  searchForm.value.questName = '';
  searchForm.value.questCode = '';
  searchForm.value.questLevel = null;
  searchForm.value.state = null;
  currentPage.value = 1;
  fetchData();
  ElMessage.success('已重置搜索条件');
};

// 每页条数改变
const handleSizeChange = async (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
  await handleSearch();  // 使用搜索方法而不是fetchData
};
// 当前页改变
const handleCurrentChange = async (val: number) => {
  currentPage.value = val;
  await handleSearch();  // 使用搜索方法而不是fetchData
};


// 详情查看
const handleDetail = async (row: Quest) => {
  try {
    const response = await QuestApi.getQuest(row.id);
    if (response.code === 200) {
      selectedQuest.value = response.data;
      detailDialogVisible.value = true;
    } else {
      ElMessage.error('获取详情失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('获取详情失败');
    console.error('获取详情失败:', error);
  }
};



// 编辑方法
const handleEdit = (row: Quest) => {
  // 填充编辑表单数据
  editForm.value = {
    id: row.id,
    questCode: row.questCode,
    questName: row.questName,
    questLevel: row.questLevel,
    questDescription: row.questDescription,
    state: row.state,
    resolvingByTeamId: row.resolvingByTeamId,
    resolvingByTeamName: row.resolvingByTeamName,
    deadline: row.deadline,
    assignee: row.assignee,
    avatar: row.avatar,
    progress: row.progress,
    history: row.history
  };

  editDialogVisible.value = true;
};

// 删除任务
const handleDelete = async (row: Quest) => {
  try {
    await ElMessageBox.confirm(`确定删除任务 "${row.questName}" 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    const response = await QuestApi.deleteQuest(row.id);
    if (response.code === 200) {
      ElMessage.success('删除成功');
      await fetchData();
    } else {
      ElMessage.error('删除失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    // 用户取消操作
  }
};

// 打开新建任务弹窗
const openCreateDialog = () => {
  // 重置新建表单
  createForm.value = {
    questCode: '',
    questName: '',
    questLevel: 3,
    resolvingByTeamId: 0,
    resolvingByTeamName: '',
    questDescription: '',
    state: 0,
  };

  createDialogVisible.value = true;
};

// 提交新建表单
const handleCreateSubmit = async () => {
  console.log(createForm.value)
  // 验证表单
  const valid = await createFormRef.value.validate().catch(() => false);
  if (!valid) return;

  try {
    const response = await QuestApi.createQuest(createForm.value);
    if (response.code === 200) {
      ElMessage.success('创建成功');
      createDialogVisible.value = false;
      await fetchData();
    } else {
      ElMessage.error('创建失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('创建失败');
    console.error('创建失败:', error);
  }
};
// 提交编辑表单
const handleEditSubmit = async () => {
  // 验证表单
  const valid = await editFormRef.value.validate().catch(() => false);
  if (!valid) return;

  try {
    const response = await QuestApi.updateQuest(editForm.value);
    if (response.code === 200) {
      ElMessage.success('更新成功');
      editDialogVisible.value = false;
      await fetchData();
    } else {
      ElMessage.error('更新失败: ' + (response.message || '未知错误'));
    }
  } catch (error) {
    ElMessage.error('更新失败');
    console.error('更新失败:', error);
  }
};

// 任务选择小队相关
const dialogCreateTeamVisible = ref(false)
const requestTeam = ref('')
const teamList = ref()
const teamPageNum =  ref(1)
const teamPageTotal = ref(0)
const isCreate = ref(false) // 选择小队是否用于创建队伍
const handleCurrentTeamChange = (val: number) => {
  teamPageNum.value = val;
}
const handleAddAsRequestTeam = (TeamName: string) => {
  if(isCreate.value)   createForm.value.resolvingByTeamName = TeamName;
  else editForm.value.resolvingByTeamName = TeamName;
}
const handleSelectTeam = async (isCreateQuest: boolean) => {
  await catchTeamLeisure()
  isCreate.value = isCreateQuest
  dialogCreateTeamVisible.value = true;
}
const catchTeamLeisure = () =>{
  QuestApi.findTeamLeisure(teamPageNum.value,10).then(res =>{
    console.log(res);
    if(res.code === 200){
      teamList.value = res.data.list;
      teamPageTotal.value = res.data.total;
      ElMessage.success('获取空闲队伍成功')
    }else{
      ElMessage.error(`获取空闲队伍失败：${res.msg}`)
    }
  }).catch(err =>{
    console.log(err)
    ElMessage.error(`服务器错误：${err.message}`)
  })
}

</script>

<style scoped>
/* 顶部操作栏 */
.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.create-button {
  background: linear-gradient(to right, #4a6fb3, #3a5a9c);
  border: none;
  color: white;
  font-weight: bold;
  transition: all 0.3s ease;
  padding: 10px 20px;
  border-radius: 4px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.2);
}

.create-button:hover {
  background: linear-gradient(to right, #5a7fc3, #4a6aac);
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(74, 111, 179, 0.4);
}

/* 弹窗样式增强 */
.containment-dialog {
  background: linear-gradient(135deg, #0c1a33, #1a2a4a);
  border: 1px solid #4a6fb3;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(74, 111, 179, 0.5);
}

/* 详情页样式增强 */
.quest-detail {
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
}

.stamp-content {
  text-align: center;
  color: #e63946;
  font-weight: bold;
}

.stamp-title {
  font-size: 18px;
}

.stamp-level {
  font-size: 14px;
}

.header-content {
  display: flex;
  align-items: center;
}

.detail-avatar {
  margin-right: 20px;
}

.header-info {
  color: #c0d1f2;
  display: flex;
  flex-direction: column;
}

.quest-title {
  font-size: 24px;
  margin-bottom: 10px;
}

.info-row {
  display: flex;
  flex-direction: column;
  margin-bottom: 5px;
}

.description {
  margin-top: 5px;
}

.detail-section {
  margin-top: 20px;
}

.section-title {
  font-size: 18px;
  margin-bottom: 10px;
  color: #c0d1f2;
}

.mission-timeline {
  color: #c0d1f2;
}

.mission-card {
  background: rgba(10, 20, 41, 0.7);
  border: 1px solid #304878;
  color: #c0d1f2;
}

.mission-title {
  font-size: 16px;
  margin-bottom: 5px;
}

.mission-description {
  font-size: 14px;
}

/* 表格头部样式 */
.table-header-style {
  background: linear-gradient(to bottom, #1a2a4a, #0c1a33);
  color: #c0d1f2;
  font-weight: bold;
  border-bottom: 1px solid #304878;
}

.card-col {
  margin-bottom: 0px;
}

.containment-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
  border: none;
  height: 300px;
  margin-top: 5px;
}

.containment-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.avatar-container {
  position: relative;
  margin-right: 16px;
}

.security-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #fff;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.quest-avatar {
  background: #f5f7fa;
}

.card-title-wrap {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.quest-name {
  font-size: 18px;
  font-weight: 500;
  margin: 0 0 4px;
  color: #333;
}

.clearance-level {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  opacity: 0.9;
}

.card-content {
  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;

    .label {
      color: #999;

      margin-right: 8px;
      i {
        margin-right: 4px;
      }
    }

    .value {
      color: #666;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      width: 200px;
    }

    .status-tag {
      padding: 2px 6px;
      font-size: 12px;
      border-radius: 4px;
    }
  }
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;

  .edit-btn, .delete-btn {
    color: #666;
    &:hover {
      color: #409eff;
    }
  }

  .delete-btn {
    &:hover {
      color: #f56c6c;
    }
  }
}

/* 分页样式 */
.containment-pagination {
  margin-top: -10px;
  display: flex;
  justify-content: right;
}
</style>
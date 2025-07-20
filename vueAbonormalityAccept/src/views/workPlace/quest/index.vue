<template>
  <!-- 顶部操作栏 -->
  <div style="padding: 0 20px 20px;">
    <el-button
        type="primary"
        @click="openCreateDialog"
        style="border-radius: 8px; padding: 8px 20px;"
    >
      <i class="iconfont icon-plus"></i> 新建任务
    </el-button>
  </div>

  <!-- 搜索表单区域 - 暗色主题 -->
  <el-form
      :model="searchForm"
      inline
      class="search-form"
      style="margin-bottom: 16px;"
  >
    <el-form-item label="任务名称" class="search-item">
      <el-input
          v-model="searchForm.name"
          placeholder="请输入任务名称"
          clearable
          class="search-input"
      ></el-input>
    </el-form-item>

    <el-form-item label="任务等级" class="search-item">
      <el-select
          v-model="searchForm.level"
          placeholder="请选择任务等级"
          clearable
          class="search-select"
      >
        <el-option label="高级" value="高级"></el-option>
        <el-option label="中级" value="中级"></el-option>
        <el-option label="低级" value="低级"></el-option>
      </el-select>
    </el-form-item>

    <el-form-item label="任务状态" class="search-item">
      <el-select
          v-model="searchForm.status"
          placeholder="请选择任务状态"
          clearable
          class="search-select"
      >
        <el-option label="未开始" value="未开始"></el-option>
        <el-option label="进行中" value="进行中"></el-option>
        <el-option label="已完成" value="已完成"></el-option>
        <el-option label="已取消" value="已取消"></el-option>
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
    </el-form-item>
  </el-form>

  <!-- 卡片列表区域 - 任务单元风格 -->
  <div class="quest-cards">
    <el-row :gutter="30">
      <el-col
          v-for="item in originTableData"
          :key="item.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          class="card-col"
      >
        <el-card
            class="containment-card"
            :body-style="{ padding: '20px' }"
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
                  :src="item.avatar"
                  class="quest-avatar"
              />
            </div>
            <div class="card-title-wrap">
              <h3 class="quest-name">{{ item.questName }}</h3>
              <el-tag
                  :type="getLevelTagType(item.level)"
                  class="clearance-level"
              >
                <i class="iconfont icon-security"></i> {{ item.level }}级任务
              </el-tag>
            </div>
          </div>

          <!-- 卡片内容：编号、状态、截止日期、负责人 -->
          <div class="card-content">
            <div class="info-item">
              <span class="label"><i class="iconfont icon-id"></i> 编号：</span>
              <span class="value">{{ item.id }}</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-status"></i> 状态：</span>
              <el-tag :type="getStatusTagType(item.status)" class="status-tag">
                {{ item.status }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-deadline"></i> 截止日期：</span>
              <span class="value">{{ item.deadline }}</span>
            </div>
            <div class="info-item">
              <span class="label"><i class="iconfont icon-assignee"></i> 负责人：</span>
              <span class="value">{{ item.assignee }}</span>
            </div>
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

  <!-- 分页组件区域 -->
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
            <div class="stamp-level">机密等级: {{ selectedQuest.level }}</div>
          </div>
        </div>
        <div class="header-content">
          <el-avatar :size="80" :src="selectedQuest.avatar" class="detail-avatar" />
          <div class="header-info">
            <h2 class="quest-title">{{ selectedQuest.questName }}</h2>
            <div class="info-row">
              <span><i class="iconfont icon-id"></i> 编号：{{ selectedQuest.id }}</span>
              <span><i class="iconfont icon-security"></i> 等级：
                <el-tag :type="getLevelTagType(selectedQuest.level)" class="clearance-tag">
                  {{ selectedQuest.level }}
                </el-tag>
              </span>
              <span><i class="iconfont icon-status"></i> 状态：
                <el-tag :type="getStatusTagType(selectedQuest.status)" class="status-tag">
                  {{ selectedQuest.status }}
                </el-tag>
              </span>
            </div>
            <div class="info-row">
              <span><i class="iconfont icon-deadline"></i> 截止日期：{{ selectedQuest.deadline }}</span>
              <span><i class="iconfont icon-assignee"></i> 负责人：{{ selectedQuest.assignee }}</span>
            </div>
            <div class="info-row description">
              <span><i class="iconfont icon-description"></i> 描述：{{ selectedQuest.questDescription }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h3 class="section-title"><i class="iconfont icon-progress"></i> 任务进度</h3>
        <el-progress :percentage="selectedQuest.progress" :color="getProgressColor(selectedQuest.status)"></el-progress>
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
          @click="handleButtonClick('详情弹窗-关闭', detailDialogVisible = false)"
          class="dialog-button"
      >
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>

  <!-- 新建/编辑任务弹窗 -->
  <el-dialog
      v-model="formDialogVisible"
      :title="dialogType === 'create' ? '新建任务' : '编辑任务'"
      width="60%"
      class="containment-dialog"
  >
    <el-form
        :model="formData"
        ref="formRef"
        :rules="formRules"
        label-width="120px"
        class="quest-form"
    >
      <el-form-item label="任务编号" prop="questCode">
        <el-input v-model="formData.questCode" placeholder="请输入任务编号"></el-input>
      </el-form-item>
      <el-form-item label="任务名称" prop="name">
        <el-input v-model="formData.questName" placeholder="请输入任务名称"></el-input>
      </el-form-item>

      <el-form-item label="任务等级" prop="level">
        <el-radio-group v-model="formData.level">
          <el-radio label="高级">高级</el-radio>
          <el-radio label="中级">中级</el-radio>
          <el-radio label="低级">低级</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="任务状态" prop="status">
        <el-radio-group v-model="formData.status">
          <el-radio label="未开始">未开始</el-radio>
          <el-radio label="进行中">进行中</el-radio>
          <el-radio label="已完成">已完成</el-radio>
          <el-radio label="已取消">已取消</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="截止日期" prop="deadline">
        <el-date-picker
            v-model="formData.deadline"
            type="date"
            placeholder="选择截止日期"
        ></el-date-picker>
      </el-form-item>

      <el-form-item label="负责人" prop="assignee">
        <el-input v-model="formData.assignee" placeholder="请输入负责人"></el-input>
      </el-form-item>

      <el-form-item label="任务描述" prop="description">
        <el-input
            type="textarea"
            v-model="formData.description"
            placeholder="请输入任务描述"
            :rows="4"
        ></el-input>
      </el-form-item>

      <el-form-item label="任务进度" prop="progress">
        <el-slider
            v-model="formData.progress"
            :min="0"
            :max="100"
            show-input
            input-size="small"
        ></el-slider>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button
          @click="handleButtonClick('表单弹窗-取消', formDialogVisible = false)"
          class="dialog-button"
      >
        <i class="iconfont icon-cancel"></i> 取消
      </el-button>
      <el-button
          type="primary"
          @click="handleButtonClick('表单弹窗-保存', handleSubmit())"
          class="dialog-button"
      >
        <i class="iconfont icon-save"></i> 保存
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import {computed, nextTick, onMounted, reactive, ref} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {getQuestList,deleteQuest,createQuest,updateQuest} from "@/api/quest.ts";
import request from "@/utils/requests.ts";

// 按钮点击打印函数
const handleButtonClick = (buttonName: string, action?: any) => {
  console.log(`点击了按钮：${buttonName}`);
  if (action !== undefined) action; // 执行按钮原有动作
};

// const handleSubmit = () => {
//
// }


// 模拟数据库连接
const simulateDatabaseConnection = () => {
  try {
    getQuestList(currentPage.value, pageSize.value).then((response) => {
      if(response.code == 200) {
        originTableData.value = response.data.list;
        total.value = response.data.total;
        console.log(response.data);
        ElMessage.success("数据更新成功")
      }
    });
  }catch (error) {
    //console.error('完整错误信息:', error);
    ElMessage.error('获取用户列表失败')
  }
};

// 定义任务历史接口
interface QuestHistory {
  date: string;
  action: string;
  comment: string;
}

// 定义任务接口
// interface Quest {
//   id: number;
//   name: string;
//   level: string;
//   status: string;
//   deadline: string;
//   assignee: string;
//   avatar: string;
//   description: string;
//   progress: number;
//   history: QuestHistory[];
// }

interface Quest {
  id:number
  questCode:String
  questName:String
  level: string;
  status: string;
  deadline: string;
  assignee: string;
  avatar: string;
  resolvingByTeamId:number
  resolvingByTeamName:String
  questDescription:String
  history: QuestHistory[];
  progress: number;
  state:number
}

// 搜索表单数据
const searchForm = reactive({
  name: '',
  level: '',
  status: ''
});

// 原始表格数据（模拟数据库获取）
const originTableData = ref<Quest[]>([]);

// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(8);
const total = ref(0);

// 弹窗相关
const detailDialogVisible = ref(false);
const selectedQuest = ref<Quest | null>(null);

// 表单弹窗相关
const formDialogVisible = ref(false);
const formRef = ref();
const dialogType = ref<'create' | 'edit'>('create');
const formData = reactive({
  questCode: '',
  questName: '',
  level: '中级',
  status: '未开始',
  deadline: '',
  assignee: '',
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  description: '',
  progress: 0,
  history: [] as QuestHistory[]
});

// 表单验证规则
const formRules = reactive({
  questCode: [{required: true,message: '请输入任务名称', trigger: 'blur'}],
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
  deadline: [
    { required: true, message: '请选择截止日期', trigger: 'change' }
  ],
  assignee: [
    { required: true, message: '请输入负责人', trigger: 'blur' },
    { min: 2, max: 20, message: '负责人长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  progress: [
    { required: true, message: '请设置任务进度', trigger: 'change' }
  ]
});

// 模拟从数据库获取任务数据
const fetchQuestsFromDatabase = async () => {
  await simulateDatabaseConnection();

};

// 初始化数据
onMounted(async () => {
  originTableData.value = await fetchQuestsFromDatabase()
});

// 实现搜索过滤功能
const filteredData = computed(() => {
  return originTableData.value.filter(item => {
    const nameMatch = searchForm.name ? item.questName.includes(searchForm.name) : true;
    const levelMatch = searchForm.level ? item.level === searchForm.level : true;
    const statusMatch = searchForm.status ? item.status === searchForm.status : true;
    return nameMatch && levelMatch && statusMatch;
  });
});

// 计算当前页显示的数据
const currentTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredData.value.slice(start, end);
});

// 获取任务等级对应的标签类型
const getLevelTagType = (level: string) => {
  const types: Record<string, string> = {
    '高级': 'danger',
    '中级': 'warning',
    '低级': 'success'
  };
  return types[level] || '';
};

// 获取任务状态对应的标签类型
const getStatusTagType = (status: string) => {
  const types: Record<string, string> = {
    '未开始': 'info',
    '进行中': 'warning',
    '已完成': 'success',
    '已取消': 'danger'
  };
  return types[status] || '';
};

// 获取任务进度条颜色
const getProgressColor = (status: string) => {
  const colors: Record<string, string> = {
    '未开始': '#909399',
    '进行中': '#e6a23c',
    '已完成': '#67c23a',
    '已取消': '#f56c6c'
  };
  return colors[status] || '';
};

// 搜索方法
const handleSearch = () => {
  handleButtonClick('搜索');
  currentPage.value = 1;
};

// 重置方法
const handleReset = () => {
  handleButtonClick('重置');
  searchForm.name = '';
  searchForm.level = '';
  searchForm.status = '';
  currentPage.value = 1;
};

// 每页条数改变
const handleSizeChange = (val: number) => {
  handleButtonClick(`分页-每页${val}条`);
  pageSize.value = val;
  simulateDatabaseConnection()
};

// 当前页改变
const handleCurrentChange = (val: number) => {
  handleButtonClick(`分页-第${val}页`);
  currentPage.value = val;
  simulateDatabaseConnection()
};

// 查看详情方法
const handleDetail = (row: Quest) => {
  handleButtonClick(`查看详情-${row.questName}`);
  selectedQuest.value = { ...row };
  detailDialogVisible.value = true;
};

// 编辑方法
const handleEdit = (row: Quest) => {
  handleButtonClick(`编辑-${row.questName}`);
  dialogType.value = 'edit';
  Object.assign(formData, row);
  formDialogVisible.value = true;
};

// 添加删除方法
const handleDelete = async (row: Quest) => {
  try {
    await ElMessageBox.confirm(`确定删除任务 ${row.questName} 吗？`, '删除确认');
    const response = await deleteQuest(row.id);
    if (response.code === 200) {
      ElMessage.success(response.message);
      // 重新加载数据
      simulateDatabaseConnection();
    }
  } catch (error) {
    // 取消操作
  }
};

// 打开新建任务弹窗
const openCreateDialog = () => {
  handleButtonClick('新建任务');
  dialogType.value = 'create';
  formData.questName = '';
  formData.level = '中级';
  formData.status = '未开始';
  formData.deadline = '';
  formData.assignee = '';
  formData.description = '';
  formData.progress = 0;
  formData.history = [];

  formDialogVisible.value = true;

  nextTick(() => {
    formRef.value?.resetFields();
  });
};

// 提交表单
const handleSubmit = async () => {
  try {
    const form = { ...formData };
    let response;

    if (dialogType.value === 'create') {
      response = await createQuest(form ); // 需要获取当前用户ID
    } else {
      response = await updateQuest(form);
    }

    if (response.code === 200) {
      ElMessage.success(response.message);
      formDialogVisible.value = false;
      simulateDatabaseConnection();
    }
  } catch (error) {
    ElMessage.error('操作失败');
  }
};
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
  box-shadow: 0 4px 8px rgba(230, 57, 70, 0.3);
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

.quest-title {
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

.quest-title::after {
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

/* 表单样式 */
.quest-form {
  background: rgba(15, 30, 61, 0.85);
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #304878;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.quest-form .el-form-item__label {
  color: #a0b9e0;
}

.quest-form .el-input__inner,
.quest-form .el-textarea__inner,
.quest-form .el-date-picker__editor {
  background: rgba(10, 20, 41, 0.7);
  border: 1px solid #304878;
  color: #ffffff;
}

.quest-form .el-input__inner:focus,
.quest-form .el-textarea__inner:focus,
.quest-form .el-date-picker__editor:focus {
  border-color: #4a6fb3;
  box-shadow: 0 0 5px rgba(74, 111, 179, 0.5);
}

.quest-form .el-radio__label {
  color: #e0f0ff;
}

.quest-form .el-radio__inner {
  background: rgba(10, 20, 41, 0.7);
  border: 1px solid #304878;
}

.quest-form .el-radio__input.is-checked .el-radio__inner {
  border-color: #4a6fb3;
  background: #4a6fb3;
}

.quest-form .el-slider__runway {
  background-color: rgba(10, 20, 41, 0.7);
}

.quest-form .el-slider__bar {
  background-color: #4a6fb3;
}

.quest-form .el-slider__button {
  border-color: #4a6fb3;
}

.quest-cards {
  padding: 20px;

  .el-row {
    &:after {
      content: "";
      display: table;
      clear: both;
    }
  }

  .card-col {
    margin-bottom: 30px;
  }

  .containment-card {
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    transition: all 0.3s ease;
    border: none;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 20px rgba(0,0,0,0.12);
    }
  }

  .card-header {
    display: flex;
    align-items: center;
    margin-bottom: 16px;

    .avatar-container {
      position: relative;
      margin-right: 16px;

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
        box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        i {
          color: #ff4d4f;
          font-size: 14px;
        }
      }

      .quest-avatar {
        background: #f5f7fa;
      }
    }

    .card-title-wrap {
      display: flex;
      flex-direction: column;
      justify-content: center;

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
    }
  }

  .card-content {
    .info-item {
      display: flex;
      align-items: center;
      margin-bottom: 8px;

      .label {
        color: #999;
        width: 60px;
        text-align: right;
        margin-right: 8px;
        i {
          margin-right: 4px;
        }
      }

      .value {
        color: #666;
        flex: 1;
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
}
</style>
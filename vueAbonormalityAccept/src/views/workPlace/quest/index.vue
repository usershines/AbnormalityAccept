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
          v-model="searchForm.questName"
          placeholder="请输入任务名称"
          clearable
          class="search-input"
      ></el-input>
    </el-form-item>


    <el-form-item label="任务代号" class="search-item">
      <el-input
          v-model="searchForm.questCode"
          placeholder="请输入任务代号"
          clearable
          class="search-input"
      ></el-input>
    </el-form-item>

    <el-form-item label="任务等级" class="search-item">
      <el-select
          v-model="searchForm.questLevel"
          placeholder="请选择任务等级"
          clearable
          class="search-select"
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
          placeholder="请选择任务状态"
          clearable
          class="search-select"
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
            @dblclick="handleDetail(item)"
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

          <!-- 卡片内容：编号、状态、截止日期、负责人 -->
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
            <div class="stamp-level">机密等级: {{ levelMap[selectedQuest.questLevel] || '未知' }}</div>
          </div>
        </div>
        <div class="header-content">
          <el-avatar :size="80" :src="selectedQuest.avatar" class="detail-avatar" />
          <div class="header-info">
            <h2 class="quest-title">{{ selectedQuest.questName }}</h2>
            <div class="info-row">
              <span><i class="iconfont icon-questCode"></i> 任务代号：{{ selectedQuest.questCode }}</span>
              <span><i class="iconfont icon-questLevel"></i> 等级：
              <el-tag :type="getLevelTagType(selectedQuest.questLevel)" class="level-tag">
                {{ levelMap[selectedQuest.questLevel] || '未知' }}
              </el-tag>
            </span>
              <span><i class="iconfont icon-state"></i> 状态：
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
      <el-form-item label="任务编号" prop="questCode">
        <el-input v-model="createForm.questCode" placeholder="请输入任务编号"></el-input>
      </el-form-item>
      <el-form-item label="任务名称" prop="questName">
        <el-input v-model="createForm.questName" placeholder="请输入任务名称"></el-input>
      </el-form-item>

      <el-form-item label="任务等级" prop="questLevel">
        <el-radio-group v-model="createForm.questLevel">
          <el-radio :label="1">D级</el-radio>
          <el-radio :label="2">C级</el-radio>
          <el-radio :label="3">B级</el-radio>
          <el-radio :label="4">A级</el-radio>
          <el-radio :label="5">S级</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="任务状态" prop="state">
        <el-radio-group v-model="createForm.state">
          <el-radio :label="0">未开始</el-radio>
          <el-radio :label="1">进行中</el-radio>
          <el-radio :label="2">已完成</el-radio>
          <el-radio :label="3">已取消</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="执行小队" prop="resolvingByTeamName">
        <el-input v-model="createForm.resolvingByTeamName" placeholder="请输入执行小队"></el-input>
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
      <el-form-item label="任务编号" prop="questCode">
        <el-input v-model="editForm.questCode" placeholder="请输入任务编号" disabled></el-input>
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
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  getQuestList,
  getQuest,
  createQuest,
  updateQuest,
  deleteQuest,
  searchQuests,
} from "@/api/quest.ts";


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
  resolvingByTeamName: [
    { required: true, message: '请输入执行小队', trigger: 'blur' },
  ],

});

// // 按钮点击打印函数
// const handleButtonClick = (buttonName: string, action?: any) => {
//   console.log(`点击了按钮：${buttonName}`);
//   if (action !== undefined) action(); // 执行按钮原有动作
// };

// 初始化数据
onMounted(() => {
  fetchData();
});

// 初始化数据函数
const fetchData = async () => {
  try {
    const response = await getQuestList(currentPage.value, pageSize.value);
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


// 实现搜索过滤功能
// const filteredData = computed(() => {
//   return originTableData.value.filter(item => {
//     const nameMatch = searchForm.questName ? item.questName.includes(searchForm.questName) : true;
//     const levelMatch = searchForm.questLevel? item.questLevel === searchForm.questLevel : true;
//     const statusMatch = searchForm.state ? item.state === searchForm.state : true;
//     return nameMatch && levelMatch && statusMatch;
//   });
// });



// // 计算当前页显示的数据
// const currentTableData = computed(() => {
//   const start = (currentPage.value - 1) * pageSize.value;
//   const end = start + pageSize.value;
//   return filteredData.value.slice(start, end);
// });





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

    const response = await searchQuests(questParam);
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
    const response = await getQuest(row.id);
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

    const response = await deleteQuest(row.id);
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
  // 验证表单
  const valid = await createFormRef.value.validate().catch(() => false);
  if (!valid) return;

  try {
    const response = await createQuest(createForm.value);
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
    const response = await updateQuest(editForm.value);
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
}

.quest-title {
  font-size: 24px;
  margin-bottom: 10px;
}

.info-row {
  margin-bottom: 5px;
}

.description {
  margin-top: 10px;
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

/* 卡片样式 */
.quest-cards {
  padding: 20px;
}

.card-col {
  margin-bottom: 30px;
}

.containment-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
  border: none;
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

/* 分页样式 */
.containment-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>






<!--<template>-->
<!--  &lt;!&ndash; 顶部操作栏 &ndash;&gt;-->
<!--  <div style="padding: 0 20px 20px;">-->
<!--    <el-button-->
<!--        type="primary"-->
<!--        @click="openCreateDialog"-->
<!--        style="border-radius: 8px; padding: 8px 20px;"-->
<!--    >-->
<!--      <i class="iconfont icon-plus"></i> 新建任务-->
<!--    </el-button>-->
<!--  </div>-->

<!--  &lt;!&ndash; 搜索表单区域 - 暗色主题 &ndash;&gt;-->
<!--  <el-form-->
<!--      :model="searchForm"-->
<!--      inline-->
<!--      class="search-form"-->
<!--      style="margin-bottom: 16px;"-->
<!--  >-->
<!--    <el-form-item label="任务名称" class="search-item">-->
<!--      <el-input-->
<!--          v-model="searchForm.name"-->
<!--          placeholder="请输入任务名称"-->
<!--          clearable-->
<!--          class="search-input"-->
<!--      ></el-input>-->
<!--    </el-form-item>-->

<!--    <el-form-item label="任务等级" class="search-item">-->
<!--      <el-select-->
<!--          v-model="searchForm.level"-->
<!--          placeholder="请选择任务等级"-->
<!--          clearable-->
<!--          class="search-select"-->
<!--      >-->
<!--        <el-option label="高级" value="高级"></el-option>-->
<!--        <el-option label="中级" value="中级"></el-option>-->
<!--        <el-option label="低级" value="低级"></el-option>-->
<!--      </el-select>-->
<!--    </el-form-item>-->

<!--    <el-form-item label="任务状态" class="search-item">-->
<!--      <el-select-->
<!--          v-model="searchForm.status"-->
<!--          placeholder="请选择任务状态"-->
<!--          clearable-->
<!--          class="search-select"-->
<!--      >-->
<!--        <el-option label="未开始" value="未开始"></el-option>-->
<!--        <el-option label="进行中" value="进行中"></el-option>-->
<!--        <el-option label="已完成" value="已完成"></el-option>-->
<!--        <el-option label="已取消" value="已取消"></el-option>-->
<!--      </el-select>-->
<!--    </el-form-item>-->

<!--    <el-form-item>-->
<!--      <el-button-->
<!--          type="primary"-->
<!--          @click="handleSearch"-->
<!--          class="search-button"-->
<!--      >-->
<!--        <i class="iconfont icon-search"></i> 搜索-->
<!--      </el-button>-->
<!--      <el-button-->
<!--          @click="handleReset"-->
<!--          class="reset-button"-->
<!--      >-->
<!--        <i class="iconfont icon-reset"></i> 重置-->
<!--      </el-button>-->
<!--    </el-form-item>-->
<!--  </el-form>-->

<!--  &lt;!&ndash; 卡片列表区域 - 任务单元风格 &ndash;&gt;-->
<!--  <div class="quest-cards">-->
<!--    <el-row :gutter="30">-->
<!--      <el-col-->
<!--          v-for="item in currentTableData"-->
<!--          :key="item.id"-->
<!--          :xs="24"-->
<!--          :sm="12"-->
<!--          :md="8"-->
<!--          :lg="6"-->
<!--          class="card-col"-->
<!--      >-->
<!--        <el-card-->
<!--            class="containment-card"-->
<!--            :body-style="{ padding: '20px' }"-->
<!--            @click="handleDetail(item)"-->
<!--        >-->
<!--          &lt;!&ndash; 卡片头部：头像 + 标题 + 等级标签 &ndash;&gt;-->
<!--          <div class="card-header">-->
<!--            <div class="avatar-container">-->
<!--              <div class="security-badge">-->
<!--                <i class="iconfont icon-lock"></i>-->
<!--              </div>-->
<!--              <el-avatar-->
<!--                  :size="64"-->
<!--                  :src="item.avatar"-->
<!--                  class="quest-avatar"-->
<!--              />-->
<!--            </div>-->
<!--            <div class="card-title-wrap">-->
<!--              <h3 class="quest-name">{{ item.name }}</h3>-->
<!--              <el-tag-->
<!--                  :type="getLevelTagType(item.level)"-->
<!--                  class="clearance-level"-->
<!--              >-->
<!--                <i class="iconfont icon-security"></i> {{ item.level }}级任务-->
<!--              </el-tag>-->
<!--            </div>-->
<!--          </div>-->

<!--          &lt;!&ndash; 卡片内容：编号、状态、截止日期、负责人 &ndash;&gt;-->
<!--          <div class="card-content">-->
<!--            <div class="info-item">-->
<!--              <span class="label"><i class="iconfont icon-id"></i> 编号：</span>-->
<!--              <span class="value">{{ item.id }}</span>-->
<!--            </div>-->
<!--            <div class="info-item">-->
<!--              <span class="label"><i class="iconfont icon-status"></i> 状态：</span>-->
<!--              <el-tag :type="getStatusTagType(item.status)" class="status-tag">-->
<!--                {{ item.status }}-->
<!--              </el-tag>-->
<!--            </div>-->
<!--            <div class="info-item">-->
<!--              <span class="label"><i class="iconfont icon-deadline"></i> 截止日期：</span>-->
<!--              <span class="value">{{ item.deadline }}</span>-->
<!--            </div>-->
<!--            <div class="info-item">-->
<!--              <span class="label"><i class="iconfont icon-assignee"></i> 负责人：</span>-->
<!--              <span class="value">{{ item.assignee }}</span>-->
<!--            </div>-->
<!--          </div>-->

<!--          &lt;!&ndash; 操作按钮 &ndash;&gt;-->
<!--          <div class="card-actions">-->
<!--            <el-button-->
<!--                type="text"-->
<!--                size="small"-->
<!--                @click.stop="handleEdit(item)"-->
<!--                class="edit-btn"-->
<!--            >-->
<!--              <i class="iconfont icon-edit"></i> 编辑-->
<!--            </el-button>-->
<!--            <el-button-->
<!--                type="text"-->
<!--                size="small"-->
<!--                class="delete-btn"-->
<!--                @click.stop="handleDelete(item)"-->
<!--            >-->
<!--              <i class="iconfont icon-delete"></i> 删除-->
<!--            </el-button>-->
<!--          </div>-->
<!--        </el-card>-->
<!--      </el-col>-->
<!--    </el-row>-->
<!--  </div>-->

<!--  &lt;!&ndash; 分页组件区域 &ndash;&gt;-->
<!--  <el-pagination-->
<!--      @size-change="handleSizeChange"-->
<!--      @current-change="handleCurrentChange"-->
<!--      :current-page="currentPage"-->
<!--      :page-sizes="[8, 16, 24]"-->
<!--      :page-size="pageSize"-->
<!--      layout="total, sizes, prev, pager, next, jumper"-->
<!--      :total="total"-->
<!--      prev-text="上一页"-->
<!--      next-text="下一页"-->
<!--      class="containment-pagination"-->
<!--  >-->
<!--  </el-pagination>-->

<!--  &lt;!&ndash; 任务详情弹窗 - 任务单元报告风格 &ndash;&gt;-->
<!--  <el-dialog-->
<!--      v-model="detailDialogVisible"-->
<!--      :title="`${selectedQuest?.name} - 任务档案`"-->
<!--      width="70%"-->
<!--      class="containment-dialog"-->
<!--  >-->
<!--    <div v-if="selectedQuest" class="quest-detail">-->
<!--      <div class="detail-header">-->
<!--        <div class="security-stamp">-->
<!--          <div class="stamp-content">-->
<!--            <div class="stamp-title">QUEST MANAGEMENT</div>-->
<!--            <div class="stamp-level">机密等级: {{ selectedQuest.level }}</div>-->
<!--          </div>-->
<!--        </div>-->
<!--        <div class="header-content">-->
<!--          <el-avatar :size="80" :src="selectedQuest.avatar" class="detail-avatar" />-->
<!--          <div class="header-info">-->
<!--            <h2 class="quest-title">{{ selectedQuest.name }}</h2>-->
<!--            <div class="info-row">-->
<!--              <span><i class="iconfont icon-id"></i> 编号：{{ selectedQuest.id }}</span>-->
<!--              <span><i class="iconfont icon-security"></i> 等级：-->
<!--                <el-tag :type="getLevelTagType(selectedQuest.level)" class="clearance-tag">-->
<!--                  {{ selectedQuest.level }}-->
<!--                </el-tag>-->
<!--              </span>-->
<!--              <span><i class="iconfont icon-status"></i> 状态：-->
<!--                <el-tag :type="getStatusTagType(selectedQuest.status)" class="status-tag">-->
<!--                  {{ selectedQuest.status }}-->
<!--                </el-tag>-->
<!--              </span>-->
<!--            </div>-->
<!--            <div class="info-row">-->
<!--              <span><i class="iconfont icon-deadline"></i> 截止日期：{{ selectedQuest.deadline }}</span>-->
<!--              <span><i class="iconfont icon-assignee"></i> 负责人：{{ selectedQuest.assignee }}</span>-->
<!--            </div>-->
<!--            <div class="info-row description">-->
<!--              <span><i class="iconfont icon-description"></i> 描述：{{ selectedQuest.description }}</span>-->
<!--            </div>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->

<!--      <div class="detail-section">-->
<!--        <h3 class="section-title"><i class="iconfont icon-progress"></i> 任务进度</h3>-->
<!--        <el-progress :percentage="selectedQuest.progress" :color="getProgressColor(selectedQuest.status)"></el-progress>-->
<!--      </div>-->

<!--      <div class="detail-section">-->
<!--        <h3 class="section-title"><i class="iconfont icon-history"></i> 任务历史</h3>-->
<!--        <el-timeline class="mission-timeline">-->
<!--          <el-timeline-item-->
<!--              v-for="(history, index) in selectedQuest.history"-->
<!--              :key="index"-->
<!--              :timestamp="history.date"-->
<!--              placement="top"-->
<!--          >-->
<!--            <el-card class="mission-card">-->
<!--              <h4 class="mission-title">{{ history.action }}</h4>-->
<!--              <p class="mission-description">{{ history.comment }}</p>-->
<!--            </el-card>-->
<!--          </el-timeline-item>-->
<!--        </el-timeline>-->
<!--      </div>-->
<!--    </div>-->

<!--    <template #footer>-->
<!--      <el-button-->
<!--          @click="handleButtonClick('详情弹窗-关闭', detailDialogVisible = false)"-->
<!--          class="dialog-button"-->
<!--      >-->
<!--        <i class="iconfont icon-close"></i> 关闭-->
<!--      </el-button>-->
<!--    </template>-->
<!--  </el-dialog>-->

<!--  &lt;!&ndash; 新建/编辑任务弹窗 &ndash;&gt;-->
<!--  <el-dialog-->
<!--      v-model="formDialogVisible"-->
<!--      :title="dialogType === 'create' ? '新建任务' : '编辑任务'"-->
<!--      width="60%"-->
<!--      class="containment-dialog"-->
<!--  >-->
<!--    <el-form-->
<!--        :model="formData"-->
<!--        ref="formRef"-->
<!--        :rules="formRules"-->
<!--        label-width="120px"-->
<!--        class="quest-form"-->
<!--    >-->
<!--      <el-form-item label="任务名称" prop="name">-->
<!--        <el-input v-model="formData.name" placeholder="请输入任务名称"></el-input>-->
<!--      </el-form-item>-->

<!--      <el-form-item label="任务等级" prop="level">-->
<!--        <el-radio-group v-model="formData.level">-->
<!--          <el-radio label="高级">高级</el-radio>-->
<!--          <el-radio label="中级">中级</el-radio>-->
<!--          <el-radio label="低级">低级</el-radio>-->
<!--        </el-radio-group>-->
<!--      </el-form-item>-->

<!--      <el-form-item label="任务状态" prop="status">-->
<!--        <el-radio-group v-model="formData.status">-->
<!--          <el-radio label="未开始">未开始</el-radio>-->
<!--          <el-radio label="进行中">进行中</el-radio>-->
<!--          <el-radio label="已完成">已完成</el-radio>-->
<!--          <el-radio label="已取消">已取消</el-radio>-->
<!--        </el-radio-group>-->
<!--      </el-form-item>-->

<!--      <el-form-item label="截止日期" prop="deadline">-->
<!--        <el-date-picker-->
<!--            v-model="formData.deadline"-->
<!--            type="date"-->
<!--            placeholder="选择截止日期"-->
<!--        ></el-date-picker>-->
<!--      </el-form-item>-->

<!--      <el-form-item label="负责人" prop="assignee">-->
<!--        <el-input v-model="formData.assignee" placeholder="请输入负责人"></el-input>-->
<!--      </el-form-item>-->

<!--      <el-form-item label="任务描述" prop="description">-->
<!--        <el-input-->
<!--            type="textarea"-->
<!--            v-model="formData.description"-->
<!--            placeholder="请输入任务描述"-->
<!--            :rows="4"-->
<!--        ></el-input>-->
<!--      </el-form-item>-->

<!--      <el-form-item label="任务进度" prop="progress">-->
<!--        <el-slider-->
<!--            v-model="formData.progress"-->
<!--            :min="0"-->
<!--            :max="100"-->
<!--            show-input-->
<!--            input-size="small"-->
<!--        ></el-slider>-->
<!--      </el-form-item>-->
<!--    </el-form>-->

<!--    <template #footer>-->
<!--      <el-button-->
<!--          @click="handleButtonClick('表单弹窗-取消', formDialogVisible = false)"-->
<!--          class="dialog-button"-->
<!--      >-->
<!--        <i class="iconfont icon-cancel"></i> 取消-->
<!--      </el-button>-->
<!--      <el-button-->
<!--          type="primary"-->
<!--          @click="handleButtonClick('表单弹窗-保存', handleSubmit())"-->
<!--          class="dialog-button"-->
<!--      >-->
<!--        <i class="iconfont icon-save"></i> 保存-->
<!--      </el-button>-->
<!--    </template>-->
<!--  </el-dialog>-->
<!--</template>-->

<!--<script setup lang="ts">-->
<!--import { reactive, ref, computed, onMounted, nextTick } from 'vue';-->
<!--import { ElMessage, ElMessageBox } from 'element-plus';-->
<!--import {-->
<!--  getQuestList,-->
<!--  getQuestById,-->
<!--  addQuest,-->
<!--  updateQuest,-->
<!--  deleteQuest,-->
<!--  findQuestByCondition-->
<!--} from "@/api/quest.ts";-->
<!--import {getUserList} from "@/api/user.ts";-->

<!--// 定义任务历史接口-->
<!--interface QuestHistory {-->
<!--  date: string;-->
<!--  action: string;-->
<!--  comment: string;-->
<!--}-->

<!--// 定义任务接口-->
<!--interface Quest {-->
<!--  id: number;-->
<!--  name: string;-->
<!--  level: string;-->
<!--  status: string;-->
<!--  deadline: string;-->
<!--  assignee: string;-->
<!--  avatar: string;-->
<!--  description: string;-->
<!--  progress: number;-->
<!--  history: QuestHistory[];-->
<!--}-->

<!--// 搜索表单数据-->
<!--const searchForm = reactive({-->
<!--  name: '',-->
<!--  level: '',-->
<!--  status: ''-->
<!--});-->

<!--// 原始表格数据-->
<!--const originTableData = ref<Quest[]>([]);-->

<!--// 分页相关数据-->
<!--const currentPage = ref(1);-->
<!--const pageSize = ref(8);-->
<!--const total = ref(0);-->

<!--// 弹窗相关-->
<!--const detailDialogVisible = ref(false);-->
<!--const selectedQuest = ref<Quest | null>(null);-->

<!--// 表单弹窗相关-->
<!--const formDialogVisible = ref(false);-->
<!--const formRef = ref();-->
<!--const dialogType = ref<'create' | 'edit'>('create');-->
<!--let formData = reactive({-->
<!--  id: 0,-->
<!--  name: '',-->
<!--  level: '中级',-->
<!--  status: '未开始',-->
<!--  deadline: '',-->
<!--  assignee: '',-->
<!--  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',-->
<!--  description: '',-->
<!--  progress: 0,-->
<!--  history: [] as QuestHistory[]-->
<!--});-->

<!--// 表单验证规则-->
<!--const formRules = ref({-->
<!--  name: [-->
<!--    { required: true, message: '请输入任务名称', trigger: 'blur' },-->
<!--    { min: 2, max: 50, message: '任务名称长度在 2 到 50 个字符', trigger: 'blur' }-->
<!--  ],-->
<!--  level: [-->
<!--    { required: true, message: '请选择任务等级', trigger: 'change' }-->
<!--  ],-->
<!--  status: [-->
<!--    { required: true, message: '请选择任务状态', trigger: 'change' }-->
<!--  ],-->
<!--  deadline: [-->
<!--    { required: true, message: '请选择截止日期', trigger: 'change' }-->
<!--  ],-->
<!--  assignee: [-->
<!--    { required: true, message: '请输入负责人', trigger: 'blur' },-->
<!--    { min: 2, max: 20, message: '负责人长度在 2 到 20 个字符', trigger: 'blur' }-->
<!--  ],-->
<!--  progress: [-->
<!--    { required: true, message: '请设置任务进度', trigger: 'change' }-->
<!--  ]-->
<!--});-->

<!--// 按钮点击打印函数-->
<!--const handleButtonClick = (buttonName: string, action?: any) => {-->
<!--  console.log(`点击了按钮：${buttonName}`);-->
<!--  if (action !== undefined) action(); // 执行按钮原有动作-->
<!--};-->

<!--// // 初始化数据-->
<!--// onMounted(async () => {-->
<!--//   await fetchQuests();-->
<!--// });-->
<!--//-->
<!--// // 获取任务列表-->
<!--// const fetchQuests = async () => {-->
<!--//   try {-->
<!--//     const questParam = {-->
<!--//       pageNum: currentPage.value,-->
<!--//       pageSize: pageSize.value,-->
<!--//       ...searchForm-->
<!--//     };-->
<!--//     const response = await findQuestByCondition(questParam);-->
<!--//     originTableData.value = response.data.data.list;-->
<!--//     total.value = response.data.data.total;-->
<!--//   } catch (error) {-->
<!--//     console.error('获取任务列表失败', error);-->
<!--//     ElMessage.error('获取任务列表失败');-->
<!--//   }-->
<!--// };-->
<!--// 生命周期钩子-->
<!--const catchData = () => {-->
<!--  getQuestList(currentPage.value, pageSize.value).then((response) => {-->
<!--    if(response.code === 200) {-->
<!--      formData.value = response.data.list;-->
<!--      total.value = response.data.total;-->
<!--      ElMessage.success('数据更新成功')-->
<!--      console.log('获取数据',response)-->
<!--    }else{-->
<!--      ElMessage.error('发生错误：',response.message);-->
<!--    }-->
<!--  }).catch((e) => {-->
<!--        console.log('错误',e)-->
<!--        ElMessage.error(e.message);-->
<!--      }-->
<!--  )-->
<!--}-->
<!--onMounted(() =>{-->
<!--  // 初始化表单-->
<!--  catchData()-->

<!--})-->
<!--// // 实现搜索过滤功能-->
<!--// const filteredData = computed(() => {-->
<!--//   return originTableData.value.filter(item => {-->
<!--//     const nameMatch = searchForm.name ? item.name.includes(searchForm.name) : true;-->
<!--//     const levelMatch = searchForm.level ? item.level === searchForm.level : true;-->
<!--//     const statusMatch = searchForm.status ? item.status === searchForm.status : true;-->
<!--//     return nameMatch && levelMatch && statusMatch;-->
<!--//   });-->
<!--// });-->
<!--// 实现搜索过滤功能-->
<!--const handleSearch = () => {-->
<!--  console.log('搜索表单',searchForm);-->
<!--  findQuestByCondition(searchForm).then((response) => {-->
<!--    if(response.code === 200) {-->
<!--      formData = response.data.list;-->
<!--      total.value = response.data.total;-->
<!--      ElMessage.success('搜索成功')-->
<!--    }-->
<!--  }).catch((e) => {-->
<!--    console.log('error', e)-->
<!--    ElMessage.error('服务器错误',e.msg);-->
<!--  })-->
<!--}-->

<!--// // 计算当前页显示的数据-->
<!--// const currentTableData = computed(() => {-->
<!--//   const start = (currentPage.value - 1) * pageSize.value;-->
<!--//   const end = start + pageSize.value;-->
<!--//   return filteredData.value.slice(start, end);-->
<!--//   catchData()-->
<!--// });-->

<!--// 获取任务等级对应的标签类型-->
<!--const getLevelTagType = (level: string) => {-->
<!--  const types: Record<string, string> = {-->
<!--    '高级': 'danger',-->
<!--    '中级': 'warning',-->
<!--    '低级': 'success'-->
<!--  };-->
<!--  return types[level] || '';-->
<!--};-->

<!--// 获取任务状态对应的标签类型-->
<!--const getStatusTagType = (status: string) => {-->
<!--  const types: Record<string, string> = {-->
<!--    '未开始': 'info',-->
<!--    '进行中': 'warning',-->
<!--    '已完成': 'success',-->
<!--    '已取消': 'danger'-->
<!--  };-->
<!--  return types[status] || '';-->
<!--};-->

<!--// 获取任务进度条颜色-->
<!--const getProgressColor = (status: string) => {-->
<!--  const colors: Record<string, string> = {-->
<!--    '未开始': '#909399',-->
<!--    '进行中': '#e6a23c',-->
<!--    '已完成': '#67c23a',-->
<!--    '已取消': '#f56c6c'-->
<!--  };-->
<!--  return colors[status] || '';-->
<!--};-->

<!--// 搜索方法-->
<!--const handleSearch = () => {-->
<!--  handleButtonClick('搜索');-->
<!--  currentPage.value = 1;-->
<!--  catchData()-->
<!--};-->

<!--// 重置方法-->
<!--const handleReset = () => {-->
<!--  handleButtonClick('重置');-->
<!--  searchForm.name = '';-->
<!--  searchForm.level = '';-->
<!--  searchForm.status = '';-->
<!--  currentPage.value = 1;-->
<!--  catchData()-->
<!--};-->

<!--// 每页条数改变-->
<!--const handleSizeChange = (val: number) => {-->
<!--  handleButtonClick(`分页-每页${val}条`);-->
<!--  pageSize.value = val;-->
<!--  currentPage.value = 1;-->
<!--  catchData()-->
<!--};-->

<!--// 当前页改变-->
<!--const handleCurrentChange = (val: number) => {-->
<!--  handleButtonClick(`分页-第${val}页`);-->
<!--  currentPage.value = val;-->
<!--  catchData()-->
<!--};-->

<!--// 查看详情方法-->
<!--const handleDetail = async (row: Quest) => {-->
<!--  handleButtonClick(`查看详情-${row.name}`);-->
<!--  try {-->
<!--    const response = await getQuestById(row.id);-->
<!--    selectedQuest.value = response.data.data;-->
<!--    detailDialogVisible.value = true;-->
<!--  } catch (error) {-->
<!--    console.error('获取任务详情失败', error);-->
<!--    ElMessage.error('获取任务详情失败');-->
<!--  }-->
<!--};-->

<!--// 编辑方法-->
<!--const handleEdit = (row: Quest) => {-->
<!--  handleButtonClick(`编辑-${row.name}`);-->
<!--  dialogType.value = 'edit';-->
<!--  Object.assign(formData, row);-->
<!--  formDialogVisible.value = true;-->
<!--};-->

<!--// 添加删除方法-->
<!--const handleDelete = (row: Quest) => {-->
<!--  handleButtonClick(`删除-${row.name}`);-->
<!--  ElMessageBox.confirm(-->
<!--      `确定要删除任务 ${row.name} 吗？`,-->
<!--      '删除确认',-->
<!--      {-->
<!--        confirmButtonText: '确定',-->
<!--        cancelButtonText: '取消',-->
<!--        type: 'warning'-->
<!--      }-->
<!--  ).then(async () => {-->
<!--    try {-->
<!--      await deleteQuest(row.id);-->
<!--      ElMessage.success(`已删除任务: ${row.name}`);-->
<!--      await catchData();-->
<!--    } catch (error) {-->
<!--      console.error('删除任务失败', error);-->
<!--      ElMessage.error('删除任务失败');-->
<!--    }-->
<!--  }).catch(() => {-->
<!--    // 取消操作-->
<!--  });-->
<!--};-->

<!--// 打开新建任务弹窗-->
<!--const openCreateDialog = () => {-->
<!--  handleButtonClick('新建任务');-->
<!--  dialogType.value = 'create';-->
<!--  formData.id = 0;-->
<!--  formData.name = '';-->
<!--  formData.level = '中级';-->
<!--  formData.status = '未开始';-->
<!--  formData.deadline = '';-->
<!--  formData.assignee = '';-->
<!--  formData.description = '';-->
<!--  formData.progress = 0;-->
<!--  formData.history = [];-->

<!--  formDialogVisible.value = true;-->

<!--  nextTick(() => {-->
<!--    formRef.value?.resetFields();-->
<!--  });-->
<!--};-->

<!--// 提交表单-->
<!--const handleSubmit = () => {-->
<!--  formRef.value?.validate(async (valid: boolean) => {-->
<!--    if (!valid) return;-->

<!--    if (dialogType.value === 'create') {-->
<!--      const newQuest: Quest = {-->
<!--        ...formData,-->
<!--        history: [{-->
<!--          date: new Date().toISOString().split('T')[0],-->
<!--          action: '任务创建',-->
<!--          comment: '任务被创建'-->
<!--        }]-->
<!--      };-->
<!--      try {-->
<!--        const sendId = 4; // 假设发送者ID为4-->
<!--        await addQuest(newQuest, sendId);-->
<!--        ElMessage.success('任务创建成功');-->
<!--        formDialogVisible.value = false;-->
<!--        catchData()-->
<!--      } catch (error) {-->
<!--        console.error('创建任务失败', error);-->
<!--        ElMessage.error('创建任务失败');-->
<!--      }-->
<!--    } else {-->
<!--      formData.history.push({-->
<!--        date: new Date().toISOString().split('T')[0],-->
<!--        action: '任务更新',-->
<!--        comment: '任务被更新'-->
<!--      });-->
<!--      try {-->
<!--        await updateQuest(formData);-->
<!--        ElMessage.success('任务更新成功');-->
<!--        formDialogVisible.value = false;-->
<!--        catchData()-->
<!--      } catch (error) {-->
<!--        console.error('更新任务失败', error);-->
<!--        ElMessage.error('更新任务失败');-->
<!--      }-->
<!--    }-->
<!--  });-->
<!--};-->
<!--</script>-->

<!--<style scoped>-->
<!--/* 顶部操作栏 */-->
<!--.action-bar {-->
<!--  margin-bottom: 20px;-->
<!--  display: flex;-->
<!--  justify-content: flex-end;-->
<!--}-->

<!--.create-button {-->
<!--  background: linear-gradient(to right, #4a6fb3, #3a5a9c);-->
<!--  border: none;-->
<!--  color: white;-->
<!--  font-weight: bold;-->
<!--  transition: all 0.3s ease;-->
<!--  padding: 10px 20px;-->
<!--  border-radius: 4px;-->
<!--  box-shadow: 0 2px 6px rgba(0,0,0,0.2);-->
<!--}-->

<!--.create-button:hover {-->
<!--  background: linear-gradient(to right, #5a7fc3, #4a6aac);-->
<!--  transform: translateY(-2px);-->
<!--  box-shadow: 0 4px 10px rgba(74, 111, 179, 0.4);-->
<!--}-->

<!--/* 弹窗样式增强 */-->
<!--.containment-dialog {-->
<!--  background: linear-gradient(135deg, #0c1a33, #1a2a4a);-->
<!--  border: 1px solid #4a6fb3;-->
<!--  border-radius: 8px;-->
<!--  box-shadow: 0 0 20px rgba(74, 111, 179, 0.5);-->
<!--}-->

<!--/* 详情页样式增强 */-->
<!--.quest-detail {-->
<!--  position: relative;-->
<!--  background: rgba(15, 30, 61, 0.85);-->
<!--  padding: 25px;-->
<!--  border-radius: 6px;-->
<!--  border: 1px solid #304878;-->
<!--  box-shadow: 0 4px 12px rgba(0,0,0,0.2);-->
<!--}-->

<!--.security-stamp {-->
<!--  position: absolute;-->
<!--  top: -15px;-->
<!--  right: -15px;-->
<!--  width: 120px;-->
<!--  height: 120px;-->
<!--  border: 4px solid #e63946;-->
<!--  border-radius: 50%;-->
<!--  background: rgba(230, 57, 70, 0.15);-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--  transform: rotate(15deg);-->
<!--  z-index: 10;-->
<!--  overflow: hidden;-->
<!--}-->

<!--.stamp-content {-->
<!--  text-align: center;-->
<!--  color: #e63946;-->
<!--  font-weight: bold;-->
<!--}-->

<!--.stamp-title {-->
<!--  font-size: 18px;-->
<!--}-->

<!--.stamp-level {-->
<!--  font-size: 14px;-->
<!--}-->

<!--.header-content {-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--}-->

<!--.detail-avatar {-->
<!--  margin-right: 20px;-->
<!--}-->

<!--.header-info {-->
<!--  color: #c0d1f2;-->
<!--}-->

<!--.quest-title {-->
<!--  font-size: 24px;-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--.info-row {-->
<!--  margin-bottom: 5px;-->
<!--}-->

<!--.description {-->
<!--  margin-top: 10px;-->
<!--}-->

<!--.detail-section {-->
<!--  margin-top: 20px;-->
<!--}-->

<!--.section-title {-->
<!--  font-size: 18px;-->
<!--  margin-bottom: 10px;-->
<!--  color: #c0d1f2;-->
<!--}-->

<!--.mission-timeline {-->
<!--  color: #c0d1f2;-->
<!--}-->

<!--.mission-card {-->
<!--  background: rgba(10, 20, 41, 0.7);-->
<!--  border: 1px solid #304878;-->
<!--  color: #c0d1f2;-->
<!--}-->

<!--.mission-title {-->
<!--  font-size: 16px;-->
<!--  margin-bottom: 5px;-->
<!--}-->

<!--.mission-description {-->
<!--  font-size: 14px;-->
<!--}-->

<!--/* 表格头部样式 */-->
<!--.table-header-style {-->
<!--  background: linear-gradient(to bottom, #1a2a4a, #0c1a33);-->
<!--  color: #c0d1f2;-->
<!--  font-weight: bold;-->
<!--  border-bottom: 1px solid #304878;-->
<!--}-->

<!--/* 卡片样式 */-->
<!--.quest-cards {-->
<!--  padding: 20px;-->
<!--}-->

<!--.card-col {-->
<!--  margin-bottom: 30px;-->
<!--}-->

<!--.containment-card {-->
<!--  border-radius: 12px;-->
<!--  box-shadow: 0 4px 12px rgba(0,0,0,0.08);-->
<!--  transition: all 0.3s ease;-->
<!--  border: none;-->
<!--}-->

<!--.containment-card:hover {-->
<!--  transform: translateY(-4px);-->
<!--  box-shadow: 0 8px 20px rgba(0,0,0,0.12);-->
<!--}-->

<!--.card-header {-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  margin-bottom: 16px;-->
<!--}-->

<!--.avatar-container {-->
<!--  position: relative;-->
<!--  margin-right: 16px;-->
<!--}-->

<!--.security-badge {-->
<!--  position: absolute;-->
<!--  top: -8px;-->
<!--  right: -8px;-->
<!--  background: #fff;-->
<!--  border-radius: 50%;-->
<!--  width: 24px;-->
<!--  height: 24px;-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--  box-shadow: 0 2px 4px rgba(0,0,0,0.2);-->
<!--}-->

<!--.quest-avatar {-->
<!--  background: #f5f7fa;-->
<!--}-->

<!--.card-title-wrap {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  justify-content: center;-->
<!--}-->

<!--.quest-name {-->
<!--  font-size: 18px;-->
<!--  font-weight: 500;-->
<!--  margin: 0 0 4px;-->
<!--  color: #333;-->
<!--}-->

<!--.clearance-level {-->
<!--  font-size: 12px;-->
<!--  padding: 4px 8px;-->
<!--  border-radius: 4px;-->
<!--  opacity: 0.9;-->
<!--}-->

<!--.card-content {-->
<!--  .info-item {-->
<!--    display: flex;-->
<!--    align-items: center;-->
<!--    margin-bottom: 8px;-->

<!--    .label {-->
<!--      color: #999;-->
<!--      width: 60px;-->
<!--      text-align: right;-->
<!--      margin-right: 8px;-->
<!--      i {-->
<!--        margin-right: 4px;-->
<!--      }-->
<!--    }-->

<!--    .value {-->
<!--      color: #666;-->
<!--      flex: 1;-->
<!--    }-->

<!--    .status-tag {-->
<!--      padding: 2px 6px;-->
<!--      font-size: 12px;-->
<!--      border-radius: 4px;-->
<!--    }-->
<!--  }-->
<!--}-->

<!--.card-actions {-->
<!--  display: flex;-->
<!--  justify-content: flex-end;-->
<!--  margin-top: 16px;-->

<!--  .edit-btn, .delete-btn {-->
<!--    color: #666;-->
<!--    &:hover {-->
<!--      color: #409eff;-->
<!--    }-->
<!--  }-->

<!--  .delete-btn {-->
<!--    &:hover {-->
<!--      color: #f56c6c;-->
<!--    }-->
<!--  }-->
<!--}-->

<!--/* 分页样式 */-->
<!--.containment-pagination {-->
<!--  margin-top: 20px;-->
<!--  display: flex;-->
<!--  justify-content: center;-->
<!--}-->
<!--</style>-->
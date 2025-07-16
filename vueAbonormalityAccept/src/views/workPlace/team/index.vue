<template>
  <!-- 搜索表单区域 - 暗色主题 -->
  <el-card style="margin: -20px 0 0px;height: 750px;">
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
          ></el-input>
        </el-form-item>

        <el-form-item label="权限等级" class="search-item">
          <el-select
              v-model="searchForm.level"
              placeholder="请选择权限等级"
              clearable
              class="search-select"
          >
            <el-option label="O5议会" value="O5"></el-option>
            <el-option label="A级" value="A"></el-option>
            <el-option label="B级" value="B"></el-option>
            <el-option label="C级" value="C"></el-option>
            <el-option label="D级" value="D"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="所在地" class="search-item">
          <el-input
              v-model="searchForm.location"
              placeholder="请输入所在地"
              clearable
              class="search-input"
          ></el-input>
        </el-form-item>

        <el-form-item label="状态" class="search-item">
          <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              class="search-select"
          >
            <el-option label="空闲" value="空闲"></el-option>
            <el-option label="任务中" value="任务中"></el-option>
            <el-option label="无法活动" value="无法活动"></el-option>
            <el-option label="未知" value="未知"></el-option>
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
        </el-form-item>
      </el-form>
    </template>

    <!-- 卡片列表区域 - 收容单元风格 -->
    <template #default>
        <div style="margin: -10px 0 -30px; overflow-y: auto;overflow-x:hidden ; height: 630px">
            <el-row :gutter="20">
              <el-col
                  v-for="item in currentTableData"
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
                        <el-avatar :size="60" :src="item.avatar" style="margin-bottom: 0px" />
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
                      <span class="label">成员：{{ item.members.length }}人</span>
                    </div>
                    <div class="info-item">
                      <span class="value">当前任务：{{ item.currentMission || '无' }}</span>
                    </div>
                    <div class="info-item">
                      <span class="label">所在地：{{ item.location }}</span>
                    </div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
        </div>
    </template>

    <!-- 分页组件区域 -->
    <template #footer>
      <div style="margin-top: -10px;display: flex;justify-content: flex-end">
        <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[8, 16, 24]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="filteredData.length"
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
          <el-avatar :size="80" :src="selectedTeam.avatar" class="detail-avatar" />
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
              <span><i class="iconfont icon-location"></i> 所在地：{{ selectedTeam.location }}</span>
              <span><i class="iconfont icon-superior"></i> 上级：{{ selectedTeam.superior }}</span>
            </div>
            <div v-if="selectedTeam.currentMission" class="info-row mission">
              <span><i class="iconfont icon-mission"></i> 执行中任务：{{ selectedTeam.currentMission }}</span>
            </div>
            <div class="info-row description">
              <span><i class="iconfont icon-description"></i> 简介：{{ selectedTeam.description }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h3 class="section-title"><i class="iconfont icon-member"></i> 成员列表 ({{ selectedTeam.members.length }}人)</h3>
        <el-table
          :data="selectedTeam.members"
          border
          style="width: 100%"
          class="containment-table"
        >
          <el-table-column prop="name" label="姓名" width="180">
            <template #header>
              <span><i class="iconfont icon-name"></i> 姓名</span>
            </template>
          </el-table-column>
          <el-table-column prop="role" label="职位">
            <template #header>
              <span><i class="iconfont icon-role"></i> 职位</span>
            </template>
          </el-table-column>
          <el-table-column prop="clearance" label="权限等级">
            <template #header>
              <span><i class="iconfont icon-security"></i> 权限等级</span>
            </template>
            <template #default="scope">
              <el-tag :type="getLevelTagType(scope.row.clearance)" class="clearance-tag">
                {{ scope.row.clearance }}
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
          <el-table-column label="操作" width="120">
            <template #header>
              <span><i class="iconfont icon-operation"></i> 操作</span>
            </template>
            <template #default="scope">
              <el-button
                type="text"
                @click="viewMemberDetails(scope.row)"
                class="detail-btn"
              >
                <i class="iconfont icon-detail"></i> 详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="detail-section">
        <h3 class="section-title"><i class="iconfont icon-history"></i> 任务历史</h3>
        <el-timeline class="mission-timeline">
          <el-timeline-item
            v-for="(mission, index) in selectedTeam.missionHistory"
            :key="index"
            :timestamp="mission.date"
            placement="top"
          >
            <el-card class="mission-card">
              <h4 class="mission-title">{{ mission.title }}</h4>
              <p class="mission-description">{{ mission.description }}</p>
              <div class="mission-status">
                <span><i class="iconfont icon-status"></i> 状态：</span>
                <el-tag :type="mission.status === '成功' ? 'success' : 'danger'" class="status-tag">
                  {{ mission.status }}
                </el-tag>
                <span class="mission-duration"><i class="iconfont icon-time"></i> 持续时间：{{ mission.duration }}</span>
              </div>
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

  <!-- 成员详情弹窗 -->
  <el-dialog
    v-model="memberDialogVisible"
    :title="`${selectedMember?.name} - 人员档案`"
    width="50%"
    class="containment-dialog"
  >
    <div v-if="selectedMember" class="member-detail">
      <div class="member-header">
        <div class="security-stamp small">
          <div class="stamp-content">
            <div class="stamp-title">SCP FOUNDATION</div>
            <div class="stamp-level">机密等级: {{ selectedMember.clearance }}</div>
          </div>
        </div>
        <div class="header-content">
          <el-avatar :size="100" :src="selectedMember.avatar" class="member-avatar" />
          <div class="member-info">
            <h2 class="member-name">{{ selectedMember.name }}</h2>
            <div class="info-row">
              <span><i class="iconfont icon-role"></i> 职位：{{ selectedMember.role }}</span>
              <span><i class="iconfont icon-security"></i> 权限等级：
                <el-tag :type="getLevelTagType(selectedMember.clearance)" class="clearance-tag">
                  {{ selectedMember.clearance }}
                </el-tag>
              </span>
            </div>
            <div class="info-row">
              <span><i class="iconfont icon-status"></i> 状态：
                <el-tag :type="getMemberStatusTagType(selectedMember.status)" class="status-tag">
                  {{ selectedMember.status }}
                </el-tag>
              </span>
              <span><i class="iconfont icon-date"></i> 加入时间：{{ selectedMember.joinDate }}</span>
            </div>
            <div class="info-row">
              <span><i class="iconfont icon-security-level"></i> 安全许可：{{ selectedMember.securityClearance }}</span>
            </div>
            <div class="info-row description">
              <span><i class="iconfont icon-description"></i> 简介：{{ selectedMember.bio }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-section">
        <h3 class="section-title"><i class="iconfont icon-skill"></i> 技能专长</h3>
        <div class="skills">
          <el-tag
            v-for="(skill, index) in selectedMember.skills"
            :key="index"
            class="skill-tag"
          >
            {{ skill }}
          </el-tag>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button
        @click="memberDialogVisible = false"
        class="dialog-button"
      >
        <i class="iconfont icon-close"></i> 关闭
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

// 模拟数据库连接
const simulateDatabaseConnection = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve(true);
      ElMessage.success('数据库连接成功');
    }, 200);
  });
};

// 定义成员接口
interface Member {
  id: number;
  name: string;
  role: string;
  clearance: string;
  status: string;
  avatar: string;
  joinDate: string;
  securityClearance: string;
  bio: string;
  skills: string[];
}

// 定义任务历史接口
interface Mission {
  title: string;
  description: string;
  date: string;
  status: string;
  duration: string;
}

// 定义小队接口
interface Team {
  id: number;
  name: string;
  level: string;
  email: string;
  location: string;
  superior: string;
  status: string;
  avatar: string;
  description: string;
  members: Member[];
  currentMission: string;
  missionHistory: Mission[];
}

// 搜索表单数据
const searchForm = reactive({
  name: '',
  level: '',
  location: '',
  status: ''
});

// 原始表格数据（模拟数据库获取）
const originTableData = ref<Team[]>([]);

// 分页相关数据
const currentPage = ref(1);
const pageSize = ref(8);

// 弹窗相关
const detailDialogVisible = ref(false);
const memberDialogVisible = ref(false);
const selectedTeam = ref<Team | null>(null);
const selectedMember = ref<Member | null>(null);

// 模拟从数据库获取小队数据
const fetchTeamsFromDatabase = async () => {
  // 模拟数据库连接
  await simulateDatabaseConnection();

  // 返回模拟数据
  return [
    {
      id: 1,
      name: 'O5-13',
      level: 'O5',
      email: 'o5-13@scp-foundation.org',
      location: 'Site-19',
      superior: 'O5议会',
      status: '任务中',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      description: '监督者议会成员',
      currentMission: 'SCP-682收容突破应对',
      members: [
        { id: 101, name: 'O5-13', role: '监督者', clearance: 'O5', status: '在岗', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', joinDate: '2010-05-12', securityClearance: '最高机密', bio: '监督者议会成员，负责Site-19管理', skills: ['战略决策', '异常评估', '资源管理'] },
        { id: 102, name: 'Dr. Bright', role: '高级研究员', clearance: 'A', status: '任务中', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', joinDate: '2015-08-23', securityClearance: '机密', bio: 'SCP-963持有者，异常实体专家', skills: ['实体收容', '跨维度研究', '紧急响应'] }
      ],
      missionHistory: [
        { title: 'SCP-096收容失效', description: '处理SCP-096在Site-19的收容失效事件', date: '2023-05-15', status: '成功', duration: '8小时' },
        { title: 'Site-81安全评估', description: '对Site-81进行年度安全审查', date: '2023-03-10', status: '成功', duration: '3天' },
        { title: 'SCP-173迁移', description: '将SCP-173迁移至新收容设施', date: '2023-01-20', status: '部分成功', duration: '12小时' }
      ]
    },
    {
      id: 2,
      name: 'MTF Epsilon-11',
      level: 'B',
      email: 'epsilon11@scp-foundation.org',
      location: '机动部署',
      superior: 'O5-13',
      status: '空闲',
      avatar: 'https://cube.elemecdn.com/e/f5/3f28f2a7e22d5c5c3d7b9e0e7b3e9png.png',
      description: '九尾狐机动特遣队',
      currentMission: '',
      members: [
        { id: 201, name: '指挥官 Grant', role: '指挥官', clearance: 'B', status: '在岗', avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png', joinDate: '2018-11-05', securityClearance: '机密', bio: '九尾狐特遣队指挥官，战术专家', skills: ['战术指挥', 'CQB', '爆破'] },
        { id: 202, name: '特工 Davis', role: '医疗兵', clearance: 'C', status: '休假', avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png', joinDate: '2020-02-14', securityClearance: '受限', bio: '战地医疗专家，异常医疗处理', skills: ['急救', '异常医疗', '生物危害处理'] },
        { id: 203, name: '特工 Miller', role: '重装兵', clearance: 'C', status: '在岗', avatar: 'https://cube.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6dpng.png', joinDate: '2019-07-30', securityClearance: '受限', bio: '重武器专家，收容突破应对', skills: ['重武器', '防御战术', '装甲操作'] }
      ],
      missionHistory: [
        { title: 'Site-19收容突破', description: '应对Site-19多起Keter级收容失效', date: '2023-06-20', status: '成功', duration: '36小时' },
        { title: 'SCP-106追捕', description: '追捕并重新收容逃脱的SCP-106', date: '2023-04-05', status: '成功', duration: '18小时' }
      ]
    },
    {
      id: 3,
      name: 'MTF Alpha-1',
      level: 'A',
      email: 'alpha1@scp-foundation.org',
      location: '机动部署',
      superior: 'O5议会',
      status: '任务中',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      description: '红右手特别行动组',
      currentMission: '内部安全审查',
      members: [
        { id: 301, name: '指挥官 Reed', role: '指挥官', clearance: 'A', status: '在岗', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', joinDate: '2012-09-12', securityClearance: '最高机密', bio: '红右手指挥官，内部安全专家', skills: ['反情报', '内部调查', '特种作战'] },
        { id: 302, name: '特工 Carter', role: '情报官', clearance: 'B', status: '任务中', avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png', joinDate: '2017-05-18', securityClearance: '机密', bio: '情报分析专家，反渗透', skills: ['情报分析', '密码学', '监视'] }
      ],
      missionHistory: [
        { title: 'Site-43渗透测试', description: '对Site-43进行安全渗透测试', date: '2023-05-30', status: '成功', duration: '24小时' },
        { title: 'O5-7安保任务', description: '为O5-7会议提供高级别安保', date: '2023-04-22', status: '成功', duration: '48小时' }
      ]
    },
    {
      id: 4,
      name: '研究团队 Gamma',
      level: 'C',
      email: 'gamma@scp-foundation.org',
      location: 'Site-64',
      superior: 'Dr. Glass',
      status: '无法活动',
      avatar: 'https://cube.elemecdn.com/1/34/18c7e8f8e8f8e8f8e8f8e8f8e8f8e8.png',
      description: '收容措施优化团队',
      currentMission: '',
      members: [
        { id: 401, name: 'Dr. Shaw', role: '首席研究员', clearance: 'C', status: '医疗休假', avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png', joinDate: '2021-03-15', securityClearance: '受限', bio: '收容措施专家，物理学博士', skills: ['收容设计', '材料科学', '结构分析'] },
        { id: 402, name: 'Dr. Finch', role: '研究员', clearance: 'C', status: '在岗', avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png', joinDate: '2022-01-10', securityClearance: '受限', bio: '异常工程学专家', skills: ['工程学', '异常材料', '原型设计'] }
      ],
      missionHistory: [
        { title: 'SCP-914优化', description: '优化SCP-914的收容和测试协议', date: '2023-02-18', status: '成功', duration: '2周' },
        { title: 'SCP-173收容间升级', description: '重新设计SCP-173的收容间', date: '2022-11-05', status: '成功', duration: '3周' }
      ]
    },
    {
      id: 5,
      name: 'MTF Gamma-5',
      level: 'B',
      email: 'gamma5@scp-foundation.org',
      location: '机动部署',
      superior: 'O5-13',
      status: '未知',
      avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png',
      description: '红鲱鱼机动特遣队',
      currentMission: '深空探测任务',
      members: [
        { id: 501, name: '指挥官 Vega', role: '指挥官', clearance: 'B', status: '任务中', avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png', joinDate: '2019-08-20', securityClearance: '机密', bio: '深空异常专家，宇航员', skills: ['太空作战', '零重力战术', '外星环境'] },
        { id: 502, name: '特工 Nova', role: '领航员', clearance: 'C', status: '任务中', avatar: 'https://cube.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6dpng.png', joinDate: '2020-05-15', securityClearance: '受限', bio: '天体导航专家，异常空间现象', skills: ['天体导航', '异常空间', '通信系统'] }
      ],
      missionHistory: [
        { title: '月球异常调查', description: '调查月球背面的异常能量信号', date: '2023-01-10', status: '成功', duration: '14天' },
        { title: 'SCP-2399收容', description: '执行SCP-2399收容协议', date: '2022-09-12', status: '部分成功', duration: '30天' }
      ]
    },
    {
      id: 6,
      name: '伦理委员会',
      level: 'A',
      email: 'ethics@scp-foundation.org',
      location: 'Site-15',
      superior: 'O5-7',
      status: '空闲',
      avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png',
      description: '基金会伦理监督',
      currentMission: '',
      members: [
        { id: 601, name: 'Dr. Rights', role: '主席', clearance: 'A', status: '在岗', avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png', joinDate: '2015-07-01', securityClearance: '机密', bio: '伦理委员会主席，法学博士', skills: ['伦理学', '法律', '政策制定'] },
        { id: 602, name: 'Dr. Vale', role: '高级伦理官', clearance: 'B', status: '休假', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', joinDate: '2018-03-22', securityClearance: '机密', bio: '生物伦理学专家', skills: ['生物伦理', '医学伦理', '风险评估'] }
      ],
      missionHistory: [
        { title: 'D级人员政策修订', description: '修订D级人员使用政策', date: '2023-04-10', status: '成功', duration: '1个月' },
        { title: 'SCP-231程序审查', description: '审查SCP-231相关程序', date: '2023-02-15', status: '进行中', duration: '持续' }
      ]
    },
    {
      id: 1,
      name: 'O5-13',
      level: 'O5',
      email: 'o5-13@scp-foundation.org',
      location: 'Site-19',
      superior: 'O5议会',
      status: '任务中',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      description: '监督者议会成员',
      currentMission: 'SCP-682收容突破应对',
      members: [
        { id: 101, name: 'O5-13', role: '监督者', clearance: 'O5', status: '在岗', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', joinDate: '2010-05-12', securityClearance: '最高机密', bio: '监督者议会成员，负责Site-19管理', skills: ['战略决策', '异常评估', '资源管理'] },
        { id: 102, name: 'Dr. Bright', role: '高级研究员', clearance: 'A', status: '任务中', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', joinDate: '2015-08-23', securityClearance: '机密', bio: 'SCP-963持有者，异常实体专家', skills: ['实体收容', '跨维度研究', '紧急响应'] }
      ],
      missionHistory: [
        { title: 'SCP-096收容失效', description: '处理SCP-096在Site-19的收容失效事件', date: '2023-05-15', status: '成功', duration: '8小时' },
        { title: 'Site-81安全评估', description: '对Site-81进行年度安全审查', date: '2023-03-10', status: '成功', duration: '3天' },
        { title: 'SCP-173迁移', description: '将SCP-173迁移至新收容设施', date: '2023-01-20', status: '部分成功', duration: '12小时' }
      ]
    },
    {
      id: 2,
      name: 'MTF Epsilon-11',
      level: 'B',
      email: 'epsilon11@scp-foundation.org',
      location: '机动部署',
      superior: 'O5-13',
      status: '空闲',
      avatar: 'https://cube.elemecdn.com/e/f5/3f28f2a7e22d5c5c3d7b9e0e7b3e9png.png',
      description: '九尾狐机动特遣队',
      currentMission: '',
      members: [
        { id: 201, name: '指挥官 Grant', role: '指挥官', clearance: 'B', status: '在岗', avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png', joinDate: '2018-11-05', securityClearance: '机密', bio: '九尾狐特遣队指挥官，战术专家', skills: ['战术指挥', 'CQB', '爆破'] },
        { id: 202, name: '特工 Davis', role: '医疗兵', clearance: 'C', status: '休假', avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png', joinDate: '2020-02-14', securityClearance: '受限', bio: '战地医疗专家，异常医疗处理', skills: ['急救', '异常医疗', '生物危害处理'] },
        { id: 203, name: '特工 Miller', role: '重装兵', clearance: 'C', status: '在岗', avatar: 'https://cube.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6dpng.png', joinDate: '2019-07-30', securityClearance: '受限', bio: '重武器专家，收容突破应对', skills: ['重武器', '防御战术', '装甲操作'] }
      ],
      missionHistory: [
        { title: 'Site-19收容突破', description: '应对Site-19多起Keter级收容失效', date: '2023-06-20', status: '成功', duration: '36小时' },
        { title: 'SCP-106追捕', description: '追捕并重新收容逃脱的SCP-106', date: '2023-04-05', status: '成功', duration: '18小时' }
      ]
    },
    {
      id: 3,
      name: 'MTF Alpha-1',
      level: 'A',
      email: 'alpha1@scp-foundation.org',
      location: '机动部署',
      superior: 'O5议会',
      status: '任务中',
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      description: '红右手特别行动组',
      currentMission: '内部安全审查',
      members: [
        { id: 301, name: '指挥官 Reed', role: '指挥官', clearance: 'A', status: '在岗', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', joinDate: '2012-09-12', securityClearance: '最高机密', bio: '红右手指挥官，内部安全专家', skills: ['反情报', '内部调查', '特种作战'] },
        { id: 302, name: '特工 Carter', role: '情报官', clearance: 'B', status: '任务中', avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png', joinDate: '2017-05-18', securityClearance: '机密', bio: '情报分析专家，反渗透', skills: ['情报分析', '密码学', '监视'] }
      ],
      missionHistory: [
        { title: 'Site-43渗透测试', description: '对Site-43进行安全渗透测试', date: '2023-05-30', status: '成功', duration: '24小时' },
        { title: 'O5-7安保任务', description: '为O5-7会议提供高级别安保', date: '2023-04-22', status: '成功', duration: '48小时' }
      ]
    },
    {
      id: 4,
      name: '研究团队 Gamma',
      level: 'C',
      email: 'gamma@scp-foundation.org',
      location: 'Site-64',
      superior: 'Dr. Glass',
      status: '无法活动',
      avatar: 'https://cube.elemecdn.com/1/34/18c7e8f8e8f8e8f8e8f8e8f8e8f8e8.png',
      description: '收容措施优化团队',
      currentMission: '',
      members: [
        { id: 401, name: 'Dr. Shaw', role: '首席研究员', clearance: 'C', status: '医疗休假', avatar: 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png', joinDate: '2021-03-15', securityClearance: '受限', bio: '收容措施专家，物理学博士', skills: ['收容设计', '材料科学', '结构分析'] },
        { id: 402, name: 'Dr. Finch', role: '研究员', clearance: 'C', status: '在岗', avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png', joinDate: '2022-01-10', securityClearance: '受限', bio: '异常工程学专家', skills: ['工程学', '异常材料', '原型设计'] }
      ],
      missionHistory: [
        { title: 'SCP-914优化', description: '优化SCP-914的收容和测试协议', date: '2023-02-18', status: '成功', duration: '2周' },
        { title: 'SCP-173收容间升级', description: '重新设计SCP-173的收容间', date: '2022-11-05', status: '成功', duration: '3周' }
      ]
    },
    {
      id: 5,
      name: 'MTF Gamma-5',
      level: 'B',
      email: 'gamma5@scp-foundation.org',
      location: '机动部署',
      superior: 'O5-13',
      status: '未知',
      avatar: 'https://cube.elemecdn.com/d/2d/bd0f8d8e8c8d9f1b9f9c8d8d8d8d8d.png',
      description: '红鲱鱼机动特遣队',
      currentMission: '深空探测任务',
      members: [
        { id: 501, name: '指挥官 Vega', role: '指挥官', clearance: 'B', status: '任务中', avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png', joinDate: '2019-08-20', securityClearance: '机密', bio: '深空异常专家，宇航员', skills: ['太空作战', '零重力战术', '外星环境'] },
        { id: 502, name: '特工 Nova', role: '领航员', clearance: 'C', status: '任务中', avatar: 'https://cube.elemecdn.com/2/11/6535bcfb26e4c79b48ddde44f4b6dpng.png', joinDate: '2020-05-15', securityClearance: '受限', bio: '天体导航专家，异常空间现象', skills: ['天体导航', '异常空间', '通信系统'] }
      ],
      missionHistory: [
        { title: '月球异常调查', description: '调查月球背面的异常能量信号', date: '2023-01-10', status: '成功', duration: '14天' },
        { title: 'SCP-2399收容', description: '执行SCP-2399收容协议', date: '2022-09-12', status: '部分成功', duration: '30天' }
      ]
    },
    {
      id: 6,
      name: '伦理委员会',
      level: 'A',
      email: 'ethics@scp-foundation.org',
      location: 'Site-15',
      superior: 'O5-7',
      status: '空闲',
      avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png',
      description: '基金会伦理监督',
      currentMission: '',
      members: [
        { id: 601, name: 'Dr. Rights', role: '主席', clearance: 'A', status: '在岗', avatar: 'https://cube.elemecdn.com/3/28/bb9a72d9dafd3f4a1f9d9e5d8c4e3png.png', joinDate: '2015-07-01', securityClearance: '机密', bio: '伦理委员会主席，法学博士', skills: ['伦理学', '法律', '政策制定'] },
        { id: 602, name: 'Dr. Vale', role: '高级伦理官', clearance: 'B', status: '休假', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', joinDate: '2018-03-22', securityClearance: '机密', bio: '生物伦理学专家', skills: ['生物伦理', '医学伦理', '风险评估'] }
      ],
      missionHistory: [
        { title: 'D级人员政策修订', description: '修订D级人员使用政策', date: '2023-04-10', status: '成功', duration: '1个月' },
        { title: 'SCP-231程序审查', description: '审查SCP-231相关程序', date: '2023-02-15', status: '进行中', duration: '持续' }
      ]
    },
  ];
};

// 初始化数据
onMounted(async () => {
  const teams = await fetchTeamsFromDatabase();
  originTableData.value = teams;
});

// 实现搜索过滤功能
const filteredData = computed(() => {
  return originTableData.value.filter(item => {
    const nameMatch = searchForm.name ? item.name.includes(searchForm.name) : true;
    const levelMatch = searchForm.level ? item.level === searchForm.level : true;
    const locationMatch = searchForm.location ? item.location.includes(searchForm.location) : true;
    const statusMatch = searchForm.status ? item.status === searchForm.status : true;
    return nameMatch && levelMatch && locationMatch && statusMatch;
  });
});

// 计算当前页显示的数据
const currentTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredData.value.slice(start, end);
});

// 获取权限等级对应的标签类型
const getLevelTagType = (level: string) => {
  const types: Record<string, string> = {
    'O5': 'danger',
    'A': 'warning',
    'B': 'primary',
    'C': 'success',
    'D': 'info'
  };
  return types[level] || '';
};

// 获取小队状态对应的标签类型
const getStatusTagType = (status: string) => {
  const types: Record<string, string> = {
    '空闲': 'success',
    '任务中': 'warning',
    '无法活动': 'danger',
    '未知': 'info'
  };
  return types[status] || '';
};

// 获取成员状态对应的标签类型
const getMemberStatusTagType = (status: string) => {
  const types: Record<string, string> = {
    '在岗': 'success',
    '任务中': 'warning',
    '医疗休假': 'danger',
    '休假': 'info'
  };
  return types[status] || '';
};

// 搜索方法
const handleSearch = () => {
  currentPage.value = 1;
};

// 重置方法
const handleReset = () => {
  searchForm.name = '';
  searchForm.level = '';
  searchForm.location = '';
  searchForm.status = '';
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
const handleDetail = (row: Team) => {
  selectedTeam.value = row;
  detailDialogVisible.value = true;
};

// 查看成员详情
const viewMemberDetails = (member: Member) => {
  selectedMember.value = member;
  memberDialogVisible.value = true;
};

// 编辑方法
const handleEdit = (row: Team) => {
  ElMessage.info(`编辑小队: ${row.name}`);
  // 这里可以添加实际编辑逻辑
};

// 添加删除方法
const handleDelete = (row: Team) => {
  const index = originTableData.value.findIndex(item => item.id === row.id);
  if (index !== -1) {
    originTableData.value.splice(index, 1);
    ElMessage.warning(`已删除小队: ${row.name}`);
    // 如果删除的是当前页最后一条且不是第一页，则跳转到上一页
    if (currentTableData.value.length === 0 && currentPage.value > 1) {
      currentPage.value -= 1;
    }
  }
};
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
</style>
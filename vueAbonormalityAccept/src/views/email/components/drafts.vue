<template>
  <div class="draft-container">
    <div class="draft-header">
      <div class="header-title">
        <el-icon class="header-icon"><Edit /></el-icon>
        <h1>写邮件</h1>
      </div>
      <div class="header-actions">

        <el-button @click="clearDraft">
          <el-icon><Refresh /></el-icon> 清空
        </el-button>
        <el-button type="primary" @click="sendDraft">
          <el-icon><EditPen /></el-icon> 发送
        </el-button>

      </div>
    </div>

    <div class="draft-toolbar">
      <div style="display: flex; align-items: center">
        <el-input
            v-model="name"
            placeholder="收件人用户名"
            clearable
            style="width: 600px"
        >
          <template #prepend>收件人</template>
          <template #append>@ssp.com</template>
        </el-input>
      </div>
      <div style="display: flex;align-items: center">
        <el-input
            v-model="theme"
            placeholder="邮件主题"
            clearable
            style="width: 600px;margin-top: 10px"
        ><template #prepend> 主题</template></el-input>
      </div>
    </div>

    <div class="draft-list">
      <el-input
          v-model="content"
          style="width: 100%;height: 400px"
          autosize="{ minRows: 1, maxRows: 30}"
          type="textarea"
          placeholder="请输入邮件内容"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue';
import {Edit, EditPen, Refresh} from "@element-plus/icons-vue";
import {sendEmail} from "@/api/email.ts";

// 草稿类型定义
interface Email{
  id: number,
  state: number,
  senderId: number,
  senderName: string,
  receiverId: number,
  receiverName: string,
  theme:  string,
  content: string,
  sendTime: Date,
}

// 草稿数据
const Draft:Email = {
  id: 0,
  state: 0,
  senderId: 0,
  senderName: '',
  receiverId: 0,
  receiverName: '',
  theme: '',
  content: '',
  sendTime: new Date(),
}
const name = ref('')
const theme = ref('')
const content = ref('')


// 生命周期钩子
onMounted(() => {

});

const clearDraft = () => {
  content.value = '';
  theme.value = '';
  name.value = '';
}

const sendDraft = () => {
  Draft.sendTime = new Date();
  Draft.receiverName = name.value;
  Draft.content = content.value;
  Draft.theme = theme.value;
  sendEmail(Draft).then((res) => {
    console.log(res);
    if(res.code === 200) {
      ElMessage.success("发送成功")
    }
  }).catch((err) => {
    console.log(err);
    ElMessage.error('发送失败：'+err);
  })
}

</script>

<style scoped>
.draft-container {
  display: flex;
  flex-direction: column;
  height: 670px;
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title h1 {
  font-size: 24px;
  color: #1f2d3d;
  margin: 0;
  font-weight: 600;
}

.draft-count {
  font-size: 16px;
  color: #5d8bf4;
  font-weight: 500;
}

.header-icon {
  font-size: 28px;
  color: #5d8bf4;
  background-color: rgba(93, 139, 244, 0.1);
  border-radius: 8px;
  padding: 8px;
}

.header-actions {
  display: flex;
  gap: 15px;
  align-items: center;
}

.search-input {
  width: 300px;
  border-radius: 20px;
}

.draft-toolbar {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  padding: 12px 15px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.select-all {
  margin-right: 10px;
}

.draft-list {
  flex: 1;
  overflow-y: auto;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.draft-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
  cursor: pointer;
  transition: all 0.3s ease;
}

.draft-item:hover {
  background-color: #f8f9fc;
}

.draft-item.selected {
  background-color: #e6f0ff;
}

.draft-checkbox {
  margin-right: 15px;
}

.draft-content {
  flex: 1;
  min-width: 0;
  margin-right: 20px;
}

.draft-subject {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.draft-subject span {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.draft-preview {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.draft-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  min-width: 100px;
}

.draft-time {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.draft-size {
  font-size: 12px;
  color: #909399;
}

.empty-drafts {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #909399;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  color: #c0c4cc;
}

.empty-drafts p {
  margin-bottom: 20px;
  font-size: 16px;
}

.draft-footer {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

/* 草稿详情样式 */
.draft-detail {
  position: relative;
  background: rgba(15, 30, 61, 0.85);
  padding: 25px;
  border-radius: 6px;
  border: 1px solid #304878;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.detail-header {
  margin-bottom: 20px;
}

.draft-subject {
  margin-top: 0;
  margin-bottom: 15px;
  color: #e0f0ff;
  font-size: 24px;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
  border-bottom: 2px solid #4a6fb3;
  padding-bottom: 10px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
  flex-wrap: wrap;
}

.info-item {
  margin-right: 30px;
  margin-bottom: 8px;
  min-width: 45%;
  display: flex;
  align-items: center;
}

.info-item i {
  margin-right: 8px;
  color: #a0b9e0;
  font-size: 16px;
}

.info-item strong {
  color: #ffffff;
  margin-left: 5px;
  font-weight: 500;
}

.description {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #4a6fb3;
  color: #c0d1f2;
  width: 100%;
}

.description-text {
  color: #e0f0ff;
  line-height: 1.6;
}

/* 新建草稿弹窗样式 */
.containment-dialog {
  background: linear-gradient(135deg, #0c1a33, #1a2a4a);
  border: 1px solid #4a6fb3;
  border-radius: 8px;
  box-shadow: 0 0 20px rgba(74, 111, 179, 0.5);
}

.dialog-button {
  background: linear-gradient(to right, #4a6fb3, #3a5a9c);
  border: none;
  color: white;
  font-weight: bold;
  transition: all 0.3s ease;
}

.dialog-button:hover {
  background: linear-gradient(to right, #5a7fc3, #4a6aac);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(74, 111, 179, 0.4);
}
</style>
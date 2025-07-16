<template>
  <el-form :model="form" label-width="80px">
    <el-form-item label="收件人">
      <el-input v-model="form.to" placeholder="请输入收件人邮箱" multiple>
        <template #append>
          <el-button @click="$emit('addCc')">抄送</el-button>
          <el-button @click="$emit('addBcc')">密送</el-button>
        </template>
      </el-input>
    </el-form-item>

    <el-form-item label="抄送" v-if="showCc">
      <el-input v-model="form.cc" placeholder="请输入抄送邮箱" multiple></el-input>
    </el-form-item>

    <el-form-item label="密送" v-if="showBcc">
      <el-input v-model="form.bcc" placeholder="请输入密送邮箱" multiple></el-input>
    </el-form-item>

    <el-form-item label="主题">
      <el-input v-model="form.subject" placeholder="请输入邮件主题"></el-input>
    </el-form-item>

    <el-form-item>
      <el-input type="textarea" v-model="form.content" rows="15" placeholder="请输入邮件内容"></el-input>
    </el-form-item>

    <el-form-item>
      <el-upload
          class="upload-demo"
          action="#"
          :on-change="handleUploadChange"
          :on-remove="handleUploadRemove"
          :file-list="form.attachments"
          multiple
      >
        <el-button size="small" type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过5MB</div>
      </el-upload>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits } from 'vue'

const props = defineProps({
  form: {
    type: Object,
    required: true
  }
})

const emits = defineEmits(['saveDraft', 'sendEmail', 'addCc', 'addBcc', 'uploadChange', 'uploadRemove'])

// 控制抄送和密送字段显示
const showCc = ref(false)
const showBcc = ref(false)

// 处理上传文件变化
const handleUploadChange = (file: any, fileList: any[]) => {
  emits('uploadChange', file, fileList)
}

// 处理上传文件移除
const handleUploadRemove = (file: any, fileList: any[]) => {
  emits('uploadRemove', file, fileList)
}
</script>

<style scoped>

</style>
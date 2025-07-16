<template>
  <div class="access-denied-container">
    <div class="denied-card">
      <div class="denied-icon">
        <svg viewBox="0 0 24 24">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
        </svg>
      </div>
      <h1>访问地址无效</h1>
      <p>系统检测到无效的访问地址，请检查您的访问地址是否正确</p>
      <div class="error-details">
        <p>错误代码: <span>SCP-ACCESS-DENIED-401</span></p>
        <p>安全等级: <span>LEVEL 4</span></p>
      </div>
      <!-- 点击事件处理函数 -->
      <button @click="goToHomePage" class="retry-btn">返回主页面</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 跳转主页面
const goToHomePage = () => {
  router.push({ name: 'start' })
}
</script>

<style scoped>
.access-denied-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #1a1f4b 0%, #0c0e2a 100%);
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

.denied-card {
  background: rgba(13, 18, 48, 0.9);
  padding: 40px;
  border-radius: 16px;
  text-align: center;
  max-width: 500px;
  width: 90%;
  box-shadow:
    0 0 30px rgba(255, 107, 107, 0.5),
    inset 0 0 15px rgba(255, 107, 107, 0.3);
  border: 1px solid rgba(255, 107, 107, 0.4);
  backdrop-filter: blur(5px);
}

.denied-icon svg {
  width: 80px;
  height: 80px;
  fill: #ff6b6b;
  margin-bottom: 20px;
}

h1 {
  color: #ff6b6b;
  font-size: 32px;
  margin-bottom: 15px;
  text-shadow: 0 0 10px rgba(255, 107, 107, 0.7);
}

p {
  color: #aab6ff;
  font-size: 18px;
  margin-bottom: 25px;
  line-height: 1.6;
}

.error-details {
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid rgba(255, 107, 107, 0.3);
  border-radius: 8px;
  padding: 15px;
  margin: 30px 0;
  text-align: left;
}

.error-details p {
  font-size: 16px;
  margin-bottom: 10px;
}

.error-details span {
  color: #ff6b6b;
  font-weight: bold;
}

.retry-btn {
  background: linear-gradient(90deg, #5d8bf4, #3a5bcf);
  border: none;
  border-radius: 6px;
  color: white;
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 0 15px rgba(93, 139, 244, 0.4);
}

.retry-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 25px rgba(93, 139, 244, 0.7);
}
</style>
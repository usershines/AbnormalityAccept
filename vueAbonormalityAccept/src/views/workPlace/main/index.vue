<template>
  <div class="containment-home">
    <!-- 顶部标题 -->
    <div class="header-text">
      <h1>异想体收容管理系统</h1>
    </div>

    <!-- 异想体世界观描述 -->
    <div class="worldview-description">
      <p>在遥远的未来，世界被一种神秘而强大的存在——异想体所笼罩。这些异想体拥有着超乎想象的能力和特性，它们的出现打破了人类对现实的认知。</p>
      <p>为了维护世界的秩序与安全，人类建立了异想体收容管理系统，致力于对这些危险的存在进行收容和研究。每一个异想体都被视为一个独立的谜题，等待着我们去解开。</p>
      <p>在这个充满未知与挑战的世界里，每一秒的流逝都可能带来新的变化。而我们的使命，就是在这片混沌中寻找希望，守护人类的未来。</p>
      <p class="subtitle">此系统自运行起已过去：</p>
    </div>

    <!-- 计时器显示 -->
    <div class="timer-display">
      <div class="time-unit">
        <span class="time-value">{{ formatTime(timer.days) }}</span>
        <span class="time-label">天</span>
      </div>
      <div class="time-separator">:</div>
      <div class="time-unit">
        <span class="time-value">{{ formatTime(timer.hours) }}</span>
        <span class="time-label">小时</span>
      </div>
      <div class="time-separator">:</div>
      <div class="time-unit">
        <span class="time-value">{{ formatTime(timer.minutes) }}</span>
        <span class="time-label">分钟</span>
      </div>
      <div class="time-separator">:</div>
      <div class="time-unit">
        <span class="time-value">{{ formatTime(timer.seconds) }}</span>
        <span class="time-label">秒</span>
      </div>
    </div>

    <!-- 状态指示器 -->
    <div class="status-indicator">
      <div class="status-light green"></div>
      <span class="status-text">网站运行状态: 正常</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

// 固定起始时间（示例：2025年1月1日 00:00:00）
const startTime = ref(new Date(2025, 6, 14, 0, 0, 0))

// 计时器状态
const timer = ref({
  days: 0,
  hours: 0,
  minutes: 0,
  seconds: 0,
  running: false
})

// 计时器间隔
let interval = null

// 开始计时
const startTimer = () => {
  if (timer.value.running) return

  timer.value.running = true
  updateDuration() // 立即更新一次
  interval = setInterval(updateDuration, 1000)
}

// 停止计时
const stopTimer = () => {
  timer.value.running = false
  clearInterval(interval)
}

// 重置计时器（重新计算从起始时间到现在的持续时间）
const resetTimer = () => {
  stopTimer()
  updateDuration()
}

// 计算持续时间
const updateDuration = () => {
  const now = new Date()
  const diff = now.getTime() - startTime.value.getTime()

  // 计算天、小时、分钟、秒
  timer.value.days = Math.floor(diff / (1000 * 60 * 60 * 24))
  timer.value.hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  timer.value.minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  timer.value.seconds = Math.floor((diff % (1000 * 60)) / 1000)
}

// 格式化时间显示
const formatTime = (value) => {
  return value < 10 ? `0${value}` : value
}

// 格式化日期显示
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hours = date.getHours()
  const minutes = date.getMinutes()

  return `${year}年${formatTime(month)}月${formatTime(day)}日 ${formatTime(hours)}:${formatTime(minutes)}`
}

// 组件挂载时自动开始计时
onMounted(() => {
  startTimer()
})

// 组件卸载时清除计时器
onBeforeUnmount(() => {
  clearInterval(interval)
})
</script>

<style scoped>
/* 引入 Google Fonts 的字体 */
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@400;700&display=swap');

.containment-home {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 75vh;
  background: linear-gradient(to bottom, #000000, #1a4b8c);
  font-family: 'Orbitron', sans-serif;
  color: #e6f0ff;
  animation: fadeIn 1s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.header-text {
  text-align: center;
  margin-bottom: 1rem;
}

.header-text h1 {
  font-size: 3rem;
  color: #ffffff;
  margin-bottom: 0.2rem;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.8);
  animation: neonGlow 1.5s ease-in-out infinite alternate;
}

@keyframes neonGlow {
  from {
    text-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
  }
  to {
    text-shadow: 0 0 20px rgba(255, 255, 255, 1), 0 0 30px rgba(255, 255, 255, 1);
  }
}

.subtitle {
  font-size: 2.2rem;
  color: #aac6ff;
}

.worldview-description {
  text-align: center;
  margin-bottom: 1rem;
  max-width: 800px;
  line-height: 1.6;
  color: #aac6ff;
}

.timer-display {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 1rem;
}

.time-unit {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0 0.5rem;
  transition: transform 0.3s ease-in-out;
}

.time-unit:hover {
  transform: scale(1.1);
}

.time-value {
  font-size: 7rem;
  font-weight: bold;
  color: #ffffff;
  background-color: rgba(26, 75, 140, 0.3);
  padding: 0.5rem 1rem;
  border-radius: 12px;
  min-width: 100px;
  text-align: center;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.2);
}

.time-label {
  font-size: 1.2rem;
  color: #aac6ff;
  margin-top: 0.5rem;
}

.time-separator {
  font-size: 4rem;
  color: #ffffff;
  margin: 0 0.5rem;
  align-self: flex-end;
  padding-bottom: 1rem;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.8);
}

.status-indicator {
  display: flex;
  align-items: center;
  margin-top: 1rem;
}

.status-light {
  width: 15px;
  height: 15px;
  border-radius: 50%;
  margin-right: 0.5rem;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0.7);
  }
  70% {
    transform: scale(1);
    box-shadow: 0 0 0 10px rgba(76, 175, 80, 0);
  }
  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(76, 175, 80, 0);
  }
}

.status-light.green {
  background-color: #4caf50;
  box-shadow: 0 0 15px rgba(76, 175, 80, 0.6);
}

.status-text {
  color: #aac6ff;
  font-size: 1rem;
}
</style>
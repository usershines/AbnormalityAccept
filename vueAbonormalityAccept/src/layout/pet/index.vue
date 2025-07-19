<template>
  <div class="pet-container" ref="petContainer">
    <p class="pet-msg">{{ pet.msg }}</p>
    <img ref="petRef" @click="debounce(attckPet, 150)()" class="pet" :src="pet.src" alt="" srcset="">
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted, ref } from 'vue'
import { debounce } from 'lodash'
const imgSrc = {
  Static: import('@/assets/pic/pet/Static.gif'),
  Attack: import('@/assets/pic/pet/Attack.gif'),
  Attacked: import('@/assets/pic/pet/Attached.gif'),
  Walk: import('@/assets/pic/pet/Walk.gif'),
}
const pet = reactive({
  msg: '打我啊',
  src: imgSrc.Static,
})
const petContainer = ref<HTMLElement | null>()
const petRef = ref<HTMLElement | null>()
const mousePosition = reactive({
  x: 0,
  y: 0,
})
const petPosition = reactive({
  x: 0,
  y: 0,
})
const deg = ref<number>(0)
const deg_y = ref<number>(0)
const count = ref<number>(0)
const speed = 50
let timer = null
let isListenMouseMove = false
onMounted(async () => {
  changeSrc('Static', 'www')
})

const changeSrc = async (key, msg) => {
  let res = await imgSrc[key];
  pet.src = res.default
  pet.msg = msg
}

const attckPet = () => {
  if (isListenMouseMove) return
  changeSrc('Attacked', '啊！！！')
  window.addEventListener('mousemove', listenMouseMove)
  isListenMouseMove = true
  petPosition.x = petContainer.value?.offsetLeft
  petPosition.y = petContainer.value?.offsetTop
  setTimeout(() => {
    changeSrc('Walk', '我和你拼了')
    timer = setInterval(() => {
      move()
    }, 10);
  }, 300);
}

const listenMouseMove = (e: MouseEvent) => {
  // 需要移动的x轴距离 = 当前鼠标位置-距离浏览器左边的距离-宠物相对于浏览器页面宽度/2（宽的中心位置）
  mousePosition.x = e.x - petContainer.value.offsetLeft - petContainer.value.clientWidth / 2;
  mousePosition.y = e.y - petContainer.value.offsetTop - petContainer.value.clientHeight / 2;
  let speed = Math.ceil((Math.pow(mousePosition.x, 2) + Math.pow(mousePosition.y, 2)) / 1000);
  // 需要的旋转角度计算
  deg.value = 360 * Math.atan(mousePosition.y / mousePosition.x) / (2 * Math.PI);
  // 这里的event.clientX 返回当事件被触发时鼠标指针相对于浏览器页面（或客户区）的水平坐标。
  // 这里有关于图片位置的设置,注意你的gif图的方向，原图方向向左，那么这里就是小于，原图方向向右，这里就是大于。
  // 翻转图片
  if (petContainer.value.offsetLeft > e.clientX) {
    deg_y.value = - 180;
  } else {
    deg_y.value = 0;
  }
  //这里每一次移动鼠标都要重新计算距离，所以这里的count需要清零
  count.value = 0;
}

const move = () => {
  if (count.value < speed) {
    petPosition.x += mousePosition.x / speed
    petPosition.y += mousePosition.y / speed
    petRef.value.style.transform = "rotateZ(" + deg.value + "deg) rotateY(" + deg_y.value + "deg)"
    petContainer.value.style.left = petPosition.x + "px"
    petContainer.value.style.top = petPosition.y + "px"
    count.value++
  } else {
    window.removeEventListener('mousemove', listenMouseMove)
    changeSrc('Attack', '打死你')
    setTimeout(() => {
      changeSrc('Static', 'www')
      timer = clearInterval(timer);
      count.value = 0;
      isListenMouseMove = false;
    }, 1000);
  }
}
</script>

<style scoped lang="scss">
.pet-container {
  position: fixed;
  top: calc(100% - 100px);
  left: 0;

  .pet {
    width: 50px;
    height: 65px;
    cursor: pointer;
  }

  .pet-msg {
    padding: 5px;
    background: #8f8888;
    color: #fff;
  }
}
</style>

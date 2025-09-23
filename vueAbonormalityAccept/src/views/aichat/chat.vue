<template>
  <el-container class="chat-container">
    <!-- 左侧联系人列表 -->
    <el-aside class="contacts-sidebar" :class="{ 'mobile-visible': !showChatArea }">
      <div class="user-profile">
        <div class="user-avatar">
          <i class="fas fa-user"></i>
        </div>
        <div>
          <h3>{{ currentUser.name }}</h3>
          <p>
            在线
            <span class="connection-status" :class="isConnected ? 'connected' : 'disconnected'">
    {{ isConnected ? '已连接' : '未连接' }}
    </span>
          </p>
        </div>
      </div>

      <div class="search-box">
        <input type="text" placeholder="搜索联系人...">
      </div>

      <div class="contacts-list">
        <div
            v-for="contact in contacts"
            :key="contact.id"
            class="contact-item"
            :class="{ active: activeContact.id === contact.id }"
            @click="selectContact(contact)"
        >
          <div class="contact-avatar">
            <i class="fas fa-user"></i>
          </div>
          <div class="contact-info">
            <div class="contact-name">
              {{ contact.name }}
              <span v-if="contact.unreadCount > 0" class="unread-badge">
    {{ contact.unreadCount }}
  </span>
            </div>
            <div class="last-message">{{ contact.lastMessage }}</div>
          </div>
          <div class="contact-status">
            <i class="fas fa-circle" :class="contact.status"></i>
          </div>
        </div>
      </div>
    </el-aside>

    <!-- 右侧聊天区域 -->
    <el-main class="chat-area" :class="{ 'mobile-visible': showChatArea }">
      <div class="chat-header">
        <div class="chat-contact-avatar">
          <i class="fas fa-user"></i>
        </div>
        <div class="chat-contact-info">
          <div class="chat-contact-name">{{ activeContact.name }}</div>
          <div class="chat-contact-status">
            {{
              activeContact.status === 'online' ? '在线' :
                  activeContact.status === 'away' ? '离开' : '离线'
            }}
          </div>
        </div>
        <div class="chat-header-actions">
          <i class="fas fa-phone-alt"></i>
          <i class="fas fa-video"></i>
          <i class="fas fa-ellipsis-v"></i>
        </div>
      </div>

      <div class="messages-container" ref="messagesContainer">
        <div
            v-for="message in activeContact.messages"
            :key="message.id"
            class="message"
            :class="message.sender === 'me' ? 'sent' : 'received'"
        >
          <div class="message-content">
            <span>{{ message.content }}<span class="typing-cursor">|</span></span>
          </div>
<!--          <div class="message-time">-->
<!--            {{ formatTime(message.time) }}-->
<!--          </div>-->
        </div>
        <!-- 正在输入提示 -->
        <div class="typing-indicator" v-if="showTypingIndicator">
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
        </div>
      </div>

      <div class="message-input-area">
        <div class="message-input-tools">
          <i class="fas fa-smile"></i>
          <i class="fas fa-paperclip"></i>
          <i class="fas fa-image"></i>
        </div>
        <textarea
            class="message-input"
            placeholder="输入消息..."
            v-model="newMessage"
            @keydown="handleKeydown"
            rows="1"
        ></textarea>
        <button class="send-button" @click="sendMessage" :disabled="!newMessage.trim()">
          <i class="fas fa-paper-plane"></i>
        </button>
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick,} from 'vue';
import {postRequestStream} from "@/api/AIChat.ts";
import type { Ref } from 'vue';


// ========================== 类型定义 ==========================
interface User {
  id: number;
  name: string;
  avatar?: string;
}

interface Message {
  id: number | string;
  content: string; // 完整内容
  // time: Date;
  sender: "me" | "other";
}

interface Contact {
  id: number;
  name: string;
  avatar?: string;
  lastMessage: string;
  unreadCount: number;
  status: 'online' | 'offline' | 'away';
  messages: Message[];
}

interface SSEEvent {
  type: 'message' | 'status_update' | 'typing' | 'user_joined' | 'user_left';
  data: any;
}

// ========================== 响应式数据 ==========================
const currentUser: Ref<User> = ref({
  id: 1,
  name: '张三'
});

const contacts: Ref<Contact[]> = ref([
  {
    id: 2,
    name: '李四',
    lastMessage: '你好，最近怎么样？',
    unreadCount: 0,
    status: 'online',
    messages: [
      {
        id: 1,
        content: '你好，最近怎么样？',
        displayContent: '你好，最近怎么样？',
        sender: 'other',
        time: new Date(Date.now() - 3600000),
        type: 'text'
      },
      {
        id: 2,
        content: '我很好，谢谢关心！',
        displayContent: '我很好，谢谢关心！',
        sender: 'me',
        time: new Date(Date.now() - 3500000),
        type: 'text'
      },
      {
        id: 3,
        content: '我们什么时候见面？',
        displayContent: '我们什么时候见面？',
        sender: 'other',
        time: new Date(Date.now() - 3400000),
        type: 'text'
      }
    ]
  },
  // 其他联系人保持不变
  {
    id: 3,
    name: '王五',
    lastMessage: '项目进展如何？',
    unreadCount: 2,
    status: 'online',
    messages: [
      { id: 1, content: '项目进展如何？', displayContent: '项目进展如何？', sender: 'other', time: new Date(Date.now() - 7200000), type: 'text' },
      { id: 2, content: '进展顺利，下周可以完成。', displayContent: '进展顺利，下周可以完成。', sender: 'me', time: new Date(Date.now() - 7000000), type: 'text' }
    ]
  },
  {
    id: 4,
    name: '赵六',
    lastMessage: '周末有空吗？',
    unreadCount: 0,
    status: 'away',
    messages: [
      { id: 1, content: '周末有空吗？', displayContent: '周末有空吗？', sender: 'other', time: new Date(Date.now() - 86400000), type: 'text' },
      { id: 2, content: '有空，你有什么计划？', displayContent: '有空，你有什么计划？', sender: 'me', time: new Date(Date.now() - 86000000), type: 'text' }
    ]
  }
]);


const activeContact: Ref<Contact> = ref(contacts.value[0]);
const newMessage: Ref<string> = ref('');
const showChatArea: Ref<boolean> = ref(window.innerWidth > 768);
const messagesContainer: Ref<HTMLElement | null> = ref(null);
const isConnected: Ref<boolean> = ref(false);
const eventSource: Ref<EventSource | null> = ref(null);
let messageInterval: number | null = null; // 用于管理模拟消息定时器
// 打字机相关
const showTypingIndicator: Ref<boolean> = ref(false);
let typingTimeout: number | null = null;

// ========================== SSE相关方法 ==========================
/**
 * 连接SSE服务
 */
const connectToSSE = () => {
  try {
    console.log('正在连接到SSE服务器...');
    isConnected.value = true;

    // 实际项目中使用真实SSE连接
    eventSource.value = new EventSource('/api/chat-events');
    eventSource.value.addEventListener('message', handleSSEEvent);
    eventSource.value.addEventListener('open', () => {
      console.log('SSE连接已建立');
      isConnected.value = true;
    });
    eventSource.value.addEventListener('error', (error) => {
      console.error('SSE连接错误:', error);
      isConnected.value = false;
    });

    // 保持模拟消息以便测试
    simulateSSEMessages();
  } catch (error) {
    console.error('SSE连接失败:', error);
    isConnected.value = false;
  }
};

/**
 * 模拟SSE消息推送 - 改进版，支持打字事件
 */
const simulateSSEMessages = () => {
  if (messageInterval) clearInterval(messageInterval);

  // 每15-25秒随机发送消息或打字事件
  messageInterval = setInterval(() => {
    if (isConnected.value) {
      clearInterval(messageInterval!);
      return;
    }

    //const randomIndex = Math.floor(Math.random() * contacts.value.length);
    const randomIndex = 2;
    const randomContact = contacts.value[randomIndex];
    const isCurrentContact = randomContact.id === activeContact.value.id;

    // 30%概率发送"正在输入"事件，70%概率直接发送消息
    if (Math.random() < 0.3 && isCurrentContact) {
      // 发送打字事件
      handleSSEEvent({
        data: JSON.stringify({
          type: 'typing',
          data: {
            contactId: randomContact.id,
            isTyping: true
          }
        })
      } as MessageEvent);

      // 2-5秒后发送实际消息
      setTimeout(() => {
        const newMsgContent = getRandomMessage();
        handleSSEEvent({
          data: JSON.stringify({
            type: 'message',
            data: {
              contactId: randomContact.id,
              content: newMsgContent,
              time: new Date()
            }
          })
        } as MessageEvent);
      }, 2000 + Math.random() * 3000);
    } else if (Math.random() < 0.7) {
      // 直接发送消息
      const newMsgContent = getRandomMessage();
      handleSSEEvent({
        data: JSON.stringify({
          type: 'message',
          data: {
            contactId: randomContact.id,
            content: newMsgContent,
            time: new Date()
          }
        })
      } as MessageEvent);
    }
  }, 15000);
};

/**
 * 断开SSE连接
 */
const disconnectFromSSE = () => {
  if (eventSource.value) {
    eventSource.value.close();
    eventSource.value.removeEventListener('message', handleSSEEvent);
  }

  isConnected.value = false;
  if (messageInterval) clearInterval(messageInterval);
  if (typingTimeout) clearTimeout(typingTimeout);
  console.log('已断开SSE连接');
};

/**
 * 处理SSE事件
 */
const handleSSEEvent = (event: MessageEvent) => {
  try {
    const sseEvent: SSEEvent = JSON.parse(event.data);

    switch (sseEvent.type) {
      case 'message':
        handleIncomingMessage(sseEvent.data);
        break;
      case 'status_update':
        handleStatusUpdate(sseEvent.data);
        break;
      case 'typing':
        handleTypingIndicator(sseEvent.data);
        break;
      default:
        console.log('未知的SSE事件类型:', sseEvent.type);
    }
  } catch (error) {
    console.error('解析SSE事件失败:', error);
  }
};

// ========================== 聊天核心方法 ==========================
/**
 * 处理接收的消息 - 实现打字机效果
 */
const handleIncomingMessage = (messageData: any) => {
  console.log('收到新消息:', messageData);

  // 找到对应的联系人
  const contact = contacts.value.find(c => c.id === messageData.contactId);
  if (!contact) return;

  // 创建消息对象，初始化为正在输入状态
  const newMsg: Message = {
    id: Date.now(),
    content: messageData.content,
    sender: 'other',
    // time: messageData.time ? new Date(messageData.time) : new Date(),
  };

  // 添加到消息列表
  contact.messages.push(newMsg);
  contact.lastMessage = messageData.content;

  // 如果是当前聊天对象，更新到activeContact
  if (contact.id === activeContact.value.id) {
    activeContact.value.messages.push(newMsg);
    activeContact.value.lastMessage = messageData.content;
  } else {
    // 非当前聊天对象，增加未读计数
    contact.unreadCount += 1;
    contacts.value = [...contacts.value];
  }

  // 滚动到底部
  nextTick(scrollToBottom);

  // 实现打字机效果
  /*
  let index = 0;
  const typingSpeed = 50 + Math.random() * 30; // 每个字符50-80ms的随机延迟

  const typeInterval = setInterval(() => {
    if (index < newMsg.content.length) {
      newMsg.displayContent += newMsg.content[index];
      index++;
      // 更新消息引用以触发UI更新
      if (contact.id === activeContact.value.id) {
        activeContact.value = { ...activeContact.value };
      }
      nextTick(scrollToBottom);
    } else {
      // 打字完成
      clearInterval(typeInterval);
      newMsg.isTyping = false;
      newMsg.displayContent = newMsg.content;
      if (contact.id === activeContact.value.id) {
        activeContact.value = { ...activeContact.value };
      }
      // 清除正在输入提示
      showTypingIndicator.value = false;
    }
  }, typingSpeed);
   */

};

/**
 * 处理联系人状态更新
 */
const handleStatusUpdate = (statusData: any) => {
  console.log('状态更新:', statusData);
  const contact = contacts.value.find(c => c.id === statusData.contactId);
  if (contact) {
    contact.status = statusData.status;
    contacts.value = [...contacts.value];
    if (contact.id === activeContact.value.id) {
      activeContact.value = { ...activeContact.value };
    }
  }
};

/**
 * 处理输入指示器
 */
const handleTypingIndicator = (typingData: any) => {
  console.log('输入状态:', typingData);
  if (typingData.contactId !== activeContact.value.id) return;

  if (typingData.isTyping) {
    showTypingIndicator.value = true;
    // 5秒后自动隐藏
    if (typingTimeout) clearTimeout(typingTimeout);
    typingTimeout = window.setTimeout(() => {
      showTypingIndicator.value = false;
    }, 5000);
  } else {
    showTypingIndicator.value = false;
    if (typingTimeout) {
      clearTimeout(typingTimeout);
      typingTimeout = null;
    }
  }
};

/**
 * 发送消息
 */
const sendMessage = () => {
  const trimmedMsg = newMessage.value.trim();
  if (!trimmedMsg) return;

  const message: Message = {
    id: Date.now(),
    content: trimmedMsg,
    sender: 'me',
    // time: new Date(),
  };

  activeContact.value.messages.push(message);
  activeContact.value.lastMessage = message.content;
  newMessage.value = '';
  sendMessageToServer(message);
  nextTick(scrollToBottom);
};

const chatMsgHander = (msg : string) =>{
  console.log(msg)

}

/**
 * 发送消息到服务器（实际项目使用）
 */
const sendMessageToServer = (message: Message) => {
  console.log('发送消息到服务器:', message);
  // 实际项目中替换为API请求（如fetch/axios）
  setTimeout(() => {
    console.log('消息已送达服务器');
  }, 500);
};

/**
 * 选择聊天联系人
 */
const selectContact = (contact: Contact) => {
  activeContact.value = contact;
  // 重置未读计数
  contact.unreadCount = 0;
  // 更新响应式数组
  contacts.value = contacts.value.map(item =>
      item.id === contact.id ? { ...item, unreadCount: 0 } : item
  );

  // 移动端适配：切换到聊天界面
  if (window.innerWidth <= 768) {
    showChatArea.value = true;
  }

  // 滚动到底部
  nextTick(scrollToBottom);
};

/**
 * 滚动聊天区域到底部
 */
const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
  }
};

/**
 * 格式化消息时间
 */
const formatTime = (time: Date) => {
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();

  // 1分钟内
  if (diff < 60 * 1000) {
    return '刚刚';
  }
  // 1小时内
  else if (diff < 60 * 60 * 1000) {
    return `${Math.floor(diff / (60 * 1000))}分钟前`;
  }
  // 今天
  else if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }
  // 昨天
  else if (diff < 24 * 60 * 60 * 1000) {
    return `昨天 ${date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit'
    })}`;
  }
  // 更早时间
  else {
    return `${date.toLocaleDateString('zh-CN')} ${date.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit'
    })}`;
  }
};

/**
 * 生成随机消息（用于模拟）
 */
const getRandomMessage = () => {
  const messagePool = [
    '你好！',
    '在忙吗？',
    '这个功能很有意思',
    '我明白了，谢谢解释',
    '我们什么时候开会？',
    '项目进展如何？',
    '周末有什么计划？',
    '这个设计很棒！',
    '我需要更多信息',
    '好的，我会处理'
  ];
  return messagePool[Math.floor(Math.random() * messagePool.length)];
};

/**
 * 处理键盘事件（Enter发送消息，Shift+Enter换行）
 */
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    sendMessage();
  }
};

// ========================== 生命周期钩子 ==========================
onMounted(() => {
  // 初始化SSE连接
  postRequestStream()

  // 初始滚动到底部
  scrollToBottom();

  // 窗口大小变化监听
  const handleResize = () => {
    showChatArea.value = window.innerWidth > 768;
  };

  window.addEventListener('resize', handleResize);

  // 清理函数
  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
  });
});

onUnmounted(() => {
  // 断开SSE连接
  disconnectFromSSE();
});
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

body {
  background-color: #f0f2f5;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-container {
  width: 90%;
  max-width: 1200px;
  height: 90vh;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  display: flex;
  overflow: hidden;
}

/* 左侧联系人列表 */
.contacts-sidebar {
  width: 30%;
  min-width: 250px;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
}

.user-profile {
  padding: 20px;
  background-color: #0084ff;
  color: white;
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #005bb5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 15px;
}

.search-box {
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.search-box input {
  width: 100%;
  padding: 10px 15px;
  border-radius: 20px;
  border: 1px solid #e0e0e0;
  outline: none;
}

.contacts-list {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  padding: 15px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.contact-item:hover {
  background-color: #f5f5f5;
}

.contact-item.active {
  background-color: #e9f3ff;
}

.contact-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background-color: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 18px;
}

.contact-info {
  flex: 1;
}

.contact-name {
  font-weight: 600;
  margin-bottom: 5px;
}

.last-message {
  color: #757575;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 右侧聊天区域 */
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 15px 20px;
  border-bottom: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
}

.chat-contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.chat-contact-info {
  flex: 1;
}

.chat-contact-name {
  font-weight: 600;
}

.chat-contact-status {
  color: #757575;
  font-size: 14px;
}

.chat-header-actions {
  display: flex;
  gap: 15px;
}

.chat-header-actions i {
  font-size: 18px;
  color: #757575;
  cursor: pointer;
}

.messages-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
  display: flex;
  flex-direction: column;
}

.message {
  max-width: 70%;
  padding: 12px 15px;
  margin-bottom: 15px;
  border-radius: 18px;
  position: relative;
  word-wrap: break-word;
}

.message.received {
  align-self: flex-start;
  background-color: #fff;
  border-top-left-radius: 5px;
}

.message.sent {
  align-self: flex-end;
  background-color: #0084ff;
  color: white;
  border-top-right-radius: 5px;
}

.message-time {
  font-size: 12px;
  margin-top: 5px;
  text-align: right;
  opacity: 0.7;
}

.message-input-area {
  padding: 15px 20px;
  border-top: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  background-color: #fff;
}

.message-input-tools {
  display: flex;
  gap: 15px;
  margin-right: 15px;
}

.message-input-tools i {
  font-size: 20px;
  color: #757575;
  cursor: pointer;
}

.message-input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 24px;
  outline: none;
  resize: none;
  max-height: 120px;
  overflow-y: auto;
}

.send-button {
  margin-left: 15px;
  background-color: #0084ff;
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.connection-status {
  padding: 5px 10px;
  font-size: 12px;
  border-radius: 10px;
  margin-left: 10px;
}

.connected {
  background-color: #e7f7ef;
  color: #0caf60;
}

.disconnected {
  background-color: #fde8e8;
  color: #f14e4e;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    width: 100%;
    height: 100vh;
    border-radius: 0;
  }

  .contacts-sidebar {
    width: 100%;
    display: none;
  }

  .contacts-sidebar.mobile-visible {
    display: flex;
  }

  .chat-area {
    display: none;
  }

  .chat-area.mobile-visible {
    display: flex;
  }
}

.typing-indicator {
  align-self: flex-start;
  display: flex;
  gap: 5px;
  padding: 10px 15px;
  background-color: #fff;
  border-radius: 18px;
  margin-bottom: 15px;
}

.typing-indicator .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #999;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-indicator .dot:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-indicator .dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.typing-cursor {
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  from, to { opacity: 1; }
  50% { opacity: 0; }
}

</style>
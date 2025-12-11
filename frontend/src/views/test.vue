<template>
  <div class="petfinder-container">
    <!-- 顶部文字 -->
    <div class="description">
      Browse pets from our network of over 14,500 shelters and rescues.
    </div>
    
    <!-- 按钮区域 -->
    <div class="button-container">
      <!-- Dogs 按钮 -->
      <button class="petfinder-button" @click="navigateTo('dogs')">
        <div class="button-icon-wrapper">
          <!-- 狗图标 -->
          <svg class="button-icon" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="1.5">
            <path d="M10 8.5h4M9 11h6M10 14h4" stroke-linecap="round"/>
            <path d="M12 20.5c4 0 7-2.5 7-7v-1c0-3.5-3-6.5-7-6.5S5 9 5 12.5v1c0 4.5 3 7 7 7z" stroke-linejoin="round"/>
            <circle cx="9" cy="7.5" r="1" fill="#8B5CF6"/>
            <circle cx="15" cy="7.5" r="1" fill="#8B5CF6"/>
            <path d="M8.5 13.5l-1 3.5" stroke-linecap="round"/>
            <path d="M15.5 13.5l1 3.5" stroke-linecap="round"/>
          </svg>
        </div>
        <span class="button-label">Dogs</span>
      </button>
      
      <!-- Cats 按钮 -->
      <button class="petfinder-button" @click="navigateTo('cats')">
        <div class="button-icon-wrapper">
          <!-- 猫图标 -->
          <svg class="button-icon" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="1.5">
            <path d="M12 8.5h.01M8.5 8.5h.01M15.5 8.5h.01" stroke-linecap="round"/>
            <path d="M12 15c2.2 0 4-1.8 4-4s-1.8-4-4-4-4 1.8-4 4 1.8 4 4 4z"/>
            <path d="M8.5 13.5l-1 4M15.5 13.5l1 4" stroke-linecap="round"/>
            <path d="M9 19.5h6" stroke-linecap="round"/>
            <path d="M8 11.5l-1-1M16 11.5l1-1" stroke-linecap="round"/>
          </svg>
        </div>
        <span class="button-label">Cats</span>
      </button>
      
      <!-- Other Animals 按钮 -->
      <button class="petfinder-button" @click="navigateTo('other')">
        <div class="button-icon-wrapper">
          <!-- 爪印图标 -->
          <svg class="button-icon" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="1.5">
            <path d="M9.5 8a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zM14.5 8a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zM9.5 13a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zM14.5 13a1.5 1.5 0 1 0 0-3 1.5 1.5 0 0 0 0 3zM12 18a2 2 0 1 0 0-4 2 2 0 0 0 0 4z"/>
            <path d="M12 20.5c4 0 7-2.5 7-7v-1c0-3.5-3-6.5-7-6.5S5 9 5 12.5v1c0 4.5 3 7 7 7z" stroke-linejoin="round" fill="none"/>
          </svg>
        </div>
        <span class="button-label">Other Animals</span>
      </button>
      
      <!-- Shelters & Rescues 按钮 -->
      <button class="petfinder-button" @click="navigateTo('shelters')">
        <div class="button-icon-wrapper">
          <!-- 房屋+心形图标 -->
          <svg class="button-icon" viewBox="0 0 24 24" fill="none" stroke="#8B5CF6" stroke-width="1.5">
            <path d="M12 3l-8 6v12h16V9l-8-6z" stroke-linejoin="round"/>
            <path d="M9 21v-5a3 3 0 0 1 6 0v5" stroke-linecap="round"/>
            <path d="M10 12.5v-1a2 2 0 1 1 4 0v1" stroke-linecap="round"/>
            <path d="M12 16.5l1.2-1.2a2.5 2.5 0 1 0-2.4 0L12 16.5z" fill="#8B5CF6" stroke="none"/>
          </svg>
        </div>
        <span class="button-label">Shelters & Rescues</span>
      </button>
    </div>
  </div>
</template>

<script setup>
// 按钮点击事件处理
const navigateTo = (page) => {
  console.log(`Navigating to: ${page}`)
  // 这里可以添加路由跳转逻辑
  // 例如：router.push(`/${page}`)
}
</script>

<style scoped>
.petfinder-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 描述文字样式 */
.description {
  color: #666;
  font-size: 1.1rem;
  text-align: center;
  max-width: 600px;
  margin-bottom: 3rem;
  line-height: 1.5;
  padding: 0 1rem;
}

/* 按钮容器 */
.button-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  max-width: 1000px;
  width: 100%;
  margin: 0 auto;
}

/* 按钮基础样式 */
.petfinder-button {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 2rem 1.5rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 160px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

/* 按钮悬停效果 */
.petfinder-button:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(139, 92, 246, 0.15);
  border-color: #8B5CF6;
}

.petfinder-button:active {
  transform: translateY(-2px);
  transition: all 0.1s ease;
}

/* 图标容器 */
.button-icon-wrapper {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
  position: relative;
}

/* 图标样式 */
.button-icon {
  width: 48px;
  height: 48px;
  transition: all 0.3s ease;
}

.petfinder-button:hover .button-icon {
  transform: scale(1.1);
  stroke: #7C3AED; /* 悬停时变为深紫色 */
}

/* 按钮标签 */
.button-label {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  text-align: center;
  transition: color 0.3s ease;
}

.petfinder-button:hover .button-label {
  color: #7C3AED; /* 悬停时文字也变为深紫色 */
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .button-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
    max-width: 600px;
  }
  
  .petfinder-button {
    height: 140px;
    padding: 1.5rem 1rem;
  }
}

@media (max-width: 640px) {
  .button-container {
    grid-template-columns: 1fr;
    max-width: 300px;
  }
  
  .petfinder-button {
    height: 120px;
    flex-direction: row;
    justify-content: flex-start;
    padding: 1.5rem;
  }
  
  .button-icon-wrapper {
    width: 48px;
    height: 48px;
    margin-bottom: 0;
    margin-right: 1rem;
  }
  
  .button-icon {
    width: 40px;
    height: 40px;
  }
  
  .button-label {
    font-size: 1rem;
  }
  
  .description {
    font-size: 1rem;
    margin-bottom: 2rem;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .petfinder-container {
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
  }
  
  .description {
    color: #aaa;
  }
  
  .petfinder-button {
    background: #2d2d2d;
    border-color: #404040;
  }
  
  .button-label {
    color: #e0e0e0;
  }
}
</style>
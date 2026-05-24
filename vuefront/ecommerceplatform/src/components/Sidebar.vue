<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <h1 class="logo">综合电商平台</h1>
      <span class="subtitle">管理员后台</span>
    </div>
    
    <div class="menu-list">
      <div 
        v-for="item in menuItems" 
        :key="item.id"
        class="menu-item"
        :class="{ active: currentMenu === item.id }"
        @click="handleMenuClick(item.id)"
      >
        <span class="menu-icon">{{ item.icon }}</span>
        <span class="menu-text">{{ item.name }}</span>
        <span v-if="item.children" class="menu-arrow">▶</span>
        
        <div v-if="item.children && expandedMenus.includes(item.id)" class="submenu">
          <div 
            v-for="child in item.children" 
            :key="child.id"
            class="submenu-item"
            :class="{ active: currentSubMenu === child.id }"
            @click.stop="handleSubMenuClick(item.id, child.id)"
          >
            {{ child.name }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  currentMenu: {
    type: String,
    default: 'home'
  },
  currentSubMenu: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['menu-change', 'submenu-change'])

const expandedMenus = ref(['buyer', 'seller', 'product', 'order'])

const menuItems = [
  {
    id: 'buyer',
    name: '买家管理',
    icon: '👤',
    children: [
      { id: 'buyer-list', name: '买家列表' }
    ]
  },
  {
    id: 'seller',
    name: '卖家管理',
    icon: '🏪',
    children: [
      { id: 'seller-list', name: '卖家列表' }
    ]
  },
  {
    id: 'product',
    name: '商品管理',
    icon: '📦',
    children: [
      { id: 'product-list', name: '商品列表' }
    ]
  },
  {
    id: 'order',
    name: '订单管理',
    icon: '📋',
    children: [
      { id: 'order-list', name: '订单列表' }
    ]
  }
]

const handleMenuClick = (menuId) => {
  const item = menuItems.find(m => m.id === menuId)
  if (item && item.children) {
    const index = expandedMenus.value.indexOf(menuId)
    if (index > -1) {
      expandedMenus.value.splice(index, 1)
    } else {
      expandedMenus.value.push(menuId)
    }
  }
  emit('menu-change', menuId)
}

const handleSubMenuClick = (menuId, subMenuId) => {
  emit('menu-change', menuId)
  emit('submenu-change', subMenuId)
}
</script>

<style scoped>
.sidebar {
  width: 200px;
  height: 100vh;
  background-color: #1a237e;
  color: white;
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
}

.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
  color: #ffd700;
}

.subtitle {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

.menu-list {
  flex: 1;
  padding-top: 10px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 14px;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-item.active {
  background-color: rgba(255, 255, 255, 0.15);
  border-left: 3px solid #4fc3f7;
}

.menu-icon {
  margin-right: 10px;
  font-size: 16px;
}

.menu-text {
  flex: 1;
}

.menu-arrow {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.5);
  transition: transform 0.3s;
}

.menu-item.active .menu-arrow {
  transform: rotate(90deg);
}

.submenu {
  background-color: rgba(0, 0, 0, 0.2);
}

.submenu-item {
  padding: 10px 20px 10px 50px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: background-color 0.3s;
}

.submenu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.submenu-item.active {
  color: #4fc3f7;
  background-color: rgba(255, 255, 255, 0.1);
}
</style>
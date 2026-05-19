<template>
  <div class="admin-container">
    <div class="header">
      <h2>管理员页面</h2>
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <!-- 图片上传区域 -->
    <div class="upload-section">
      <h3>上传图片</h3>
      <input type="file" accept="image/*" @change="handleFileChange" />
      <button @click="submitUpload" :disabled="!selectedFile">上传图片</button>

      <!-- 上传成功后通过代理接口显示图片 -->
      <div v-if="imageBlobUrl" class="image-preview">
        <p>上传成功：</p>
        <img :src="imageBlobUrl" alt="上传的图片预览" />
      </div>
    </div>

    <!-- 管理员列表 -->
    <div class="admin-list">
      <div class="admin-item" v-for="admin in adminList" :key="admin.id">
        <span>ID：{{ admin.id }}</span>
        <span>用户名：{{ admin.userName }}</span>
        <span>密码：******</span>
        <button class="edit-btn" @click="openEditModal(admin)">修改密码</button>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <h3>修改密码 ID：{{ currentAdmin.id }}</h3>
        <input type="password" v-model="newPassword" placeholder="≥6位" />
        <p class="tip" v-show="newPassword.length < 6">密码不能少于6位</p>
        <div class="btns">
          <button @click="showModal = false">取消</button>
          <button @click="submitUpdatePwd">确认修改</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// ========== 管理员相关状态 ==========
const adminList = ref([])
const showModal = ref(false)
const currentAdmin = ref({})
const newPassword = ref('')

// ========== 图片上传相关状态 ==========
const selectedFile = ref(null)
const imageBlobUrl = ref('')      // 存储 blob URL 用于 img 显示
let currentBlobUrl = ''           // 辅助记录当前 blob URL（用于释放）

// ========== 退出登录 ==========
const logout = () => {
  localStorage.removeItem('admin')
  router.push('/login')
}

// ========== 生命周期 ==========
onMounted(() => {
  // 获取管理员列表
  axios.get('/api/admin/getAll').then(res => {
    adminList.value = res.data.data
  }).catch(err => {
    console.error('获取管理员列表失败', err)
    alert('获取管理员列表失败，请刷新重试')
  })
})

// 组件卸载时释放 blob URL，避免内存泄漏
onUnmounted(() => {
  if (currentBlobUrl) {
    URL.revokeObjectURL(currentBlobUrl)
  }
})

// ========== 图片上传与显示 ==========
function handleFileChange(e) {
  const file = e.target.files[0]
  if (file && file.type.startsWith('image/')) {
    selectedFile.value = file
  } else {
    alert('请选择一个图片文件')
    selectedFile.value = null
  }
}

async function submitUpload() {
  if (!selectedFile.value) {
    alert('请先选择图片文件')
    return
  }

  // 1. 构建 FormData 上传文件
  const formData = new FormData()
  formData.append('file', selectedFile.value)

  try {
    // 上传文件，后端返回完整的 COS URL
    const uploadRes = await axios.post('/api/file/upload', formData)
    const cosUrl = uploadRes.data.data   // 例如 "https://bucket.cos.region.myqcloud.com/xxx.jpg"
    if (!cosUrl) {
      throw new Error('上传返回的 URL 为空')
    }

    // 2. 调用下载代理接口，获取图片二进制流
    const downloadRes = await axios.get('/api/file/download', {
      params: { fileUrl: cosUrl },
      responseType: 'blob'   // 关键：必须以 blob 形式接收二进制数据
    })

    // 3. 检查响应是否为空或类型不正确
    if (!downloadRes.data || downloadRes.data.size === 0) {
      throw new Error('下载接口返回的图片数据为空')
    }

    // 4. 从响应头或 blob 类型中推断 MIME 类型，用于创建 Blob
    let mimeType = downloadRes.headers['content-type']
    if (!mimeType || !mimeType.startsWith('image/')) {
      // 如果后端返回通用的 octet-stream，可以根据文件后缀简单处理
      // 这里简化：默认使用 image/jpeg（实际可以更灵活）
      mimeType = 'image/jpeg'
    }

    // 5. 创建 Blob 并生成临时 URL
    const blob = new Blob([downloadRes.data], { type: mimeType })
    const newBlobUrl = URL.createObjectURL(blob)

    // 6. 释放旧的 blob URL
    if (currentBlobUrl) {
      URL.revokeObjectURL(currentBlobUrl)
    }

    // 7. 更新显示
    currentBlobUrl = newBlobUrl
    imageBlobUrl.value = newBlobUrl
    alert('上传并加载成功！')
  } catch (error) {
    console.error('图片处理失败：', error)
    if (error.response) {
      // 后端返回了错误状态码（如 404, 500）
      alert(`图片处理失败：服务器错误 ${error.response.status}`)
    } else {
      alert(`图片处理失败：${error.message}`)
    }
    // 清空显示
    if (currentBlobUrl) {
      URL.revokeObjectURL(currentBlobUrl)
      currentBlobUrl = ''
    }
    imageBlobUrl.value = ''
  }
}

// ========== 管理员修改密码 ==========
function openEditModal(admin) {
  currentAdmin.value = admin
  newPassword.value = ''
  showModal.value = true
}

async function submitUpdatePwd() {
  if (newPassword.value.length < 6) {
    alert('密码长度不能少于6位')
    return
  }
  try {
    await axios.post(`/api/admin/updatePwd/${currentAdmin.value.id}`, null, {
      params: { password: newPassword.value }
    })
    alert('修改密码成功')
    showModal.value = false
  } catch (err) {
    console.error('修改密码失败', err)
    alert('修改密码失败，请稍后重试')
  }
}
</script>

<style scoped>
.admin-container { 
  padding: 30px; 
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.logout-btn {
  background-color: #f56c6c;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.logout-btn:hover {
  background-color: #f78989;
}

.upload-section { 
  margin-bottom: 30px; 
}

.image-preview img { 
  width: 300px; 
  margin-top: 10px; 
  border-radius: 8px; 
}

.admin-item { 
  display: flex; 
  gap: 20px; 
  padding: 12px; 
  border: 1px solid #eee; 
  margin: 10px 0; 
  align-items: center;
}

.edit-btn { 
  background: #409eff; 
  color: white; 
  padding: 6px 12px; 
  border: none; 
  border-radius: 4px; 
  cursor: pointer; 
}

.modal { 
  position: fixed; 
  top: 0; 
  left: 0; 
  width: 100%; 
  height: 100%; 
  background: rgba(0,0,0,0.5); 
  display: flex; 
  align-items: center; 
  justify-content: center; 
}

.modal-content { 
  background: white; 
  padding: 30px; 
  border-radius: 8px; 
  width: 400px; 
}

.btns { 
  display: flex; 
  gap: 10px; 
  justify-content: flex-end; 
  margin-top: 10px; 
}

.tip { 
  color: red; 
  font-size: 12px; 
}
</style>
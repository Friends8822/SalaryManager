<template>
  <div style="display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:100dvh;padding:40px 24px">
    <div style="font-size:48px;margin-bottom:8px">🌱</div>
    <h2 style="color:var(--primary-dark);margin-bottom:4px">创建账号</h2>
    <p style="color:var(--text2);font-size:14px;margin-bottom:32px">开始你的春日记账之旅</p>
    <div class="card" style="width:100%">
      <div v-if="error" style="background:var(--danger);color:#fff;padding:10px 14px;border-radius:12px;font-size:13px;margin-bottom:16px">{{ error }}</div>
      <div style="margin-bottom:12px">
        <label style="font-size:14px;font-weight:600;display:block;margin-bottom:6px">用户名（≥3位）</label>
        <input class="input" v-model="username" placeholder="请输入用户名" maxlength="20" />
      </div>
      <div style="margin-bottom:12px">
        <label style="font-size:14px;font-weight:600;display:block;margin-bottom:6px">密码（≥6位）</label>
        <input class="input" v-model="password" type="password" placeholder="请输入密码" />
      </div>
      <div style="margin-bottom:16px">
        <label style="font-size:14px;font-weight:600;display:block;margin-bottom:6px">确认密码</label>
        <input class="input" v-model="password2" type="password" placeholder="请再次输入密码" @keydown.enter="register" />
      </div>
      <button class="btn" style="width:100%;margin:0" @click="register" :disabled="loading">{{ loading ? '注册中...' : '注 册' }}</button>
      <p style="text-align:center;margin-top:16px;font-size:14px;color:var(--text2)">
        已有账号？<a @click="$router.push('/login')" style="color:var(--primary-dark);cursor:pointer;font-weight:600">去登录</a>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAppStore } from '../stores/app'

const router = useRouter()
const store = useAppStore()
const username = ref(''), password = ref(''), password2 = ref('')
const error = ref(''), loading = ref(false)

async function register() {
  if (!username.value || username.value.length < 3) { error.value = '用户名至少3位'; return }
  if (!password.value || password.value.length < 6) { error.value = '密码至少6位'; return }
  if (password.value !== password2.value) { error.value = '两次密码不一致'; return }
  loading.value = true; error.value = ''
  try {
    await store.register(username.value, password.value)
    alert('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    error.value = e.message
  } finally { loading.value = false }
}
</script>

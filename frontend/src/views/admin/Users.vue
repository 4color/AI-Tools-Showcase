<template>
  <div>
    <div class="flex items-center justify-between mb-6">
      <div>
        <h2 class="text-2xl font-bold text-slate-900">用户管理</h2>
        <p class="text-slate-600 mt-1">管理系统用户信息</p>
      </div>
    </div>

    <!-- Search -->
    <div class="bg-white rounded-xl border border-slate-100 p-4 mb-6">
      <div class="flex flex-col sm:flex-row gap-4">
        <div class="flex-1 relative">
          <svg class="w-5 h-5 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="11" cy="11" r="8"/>
            <path d="m21 21-4.35-4.35"/>
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索用户名或邮箱..."
            class="w-full pl-10 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
          >
        </div>
        <button
          type="button"
          class="px-4 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 transition-colors"
          :disabled="loading"
          @click="loadUsers"
        >
          {{ loading ? '加载中...' : '查询' }}
        </button>
      </div>
    </div>

    <!-- Users Table -->
    <div class="bg-white rounded-xl border border-slate-100 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-slate-50 border-b border-slate-200">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">用户</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">邮箱</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">角色</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">Office ID</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">注册时间</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-slate-600 uppercase">操作</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-100">
            <tr v-for="user in users" :key="user.id" class="hover:bg-slate-50">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-primary-600 font-semibold">
                    {{ user.username ? user.username.charAt(0) : '?' }}
                  </div>
                  <span class="font-medium text-slate-900">{{ user.username }}</span>
                </div>
              </td>
              <td class="px-6 py-4 text-sm text-slate-600">{{ user.email }}</td>
              <td class="px-6 py-4">
                <span class="text-xs font-medium px-2 py-1 rounded"
                  :class="isAdmin(user) ? 'bg-purple-100 text-purple-700' : 'bg-slate-100 text-slate-700'">
                  {{ isAdmin(user) ? '管理员' : '用户' }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-slate-600">{{ user.officeId || '—' }}</td>
              <td class="px-6 py-4 text-sm text-slate-600">{{ formatDate(user.createdAt) }}</td>
              <td class="px-6 py-4">
                <div class="flex items-center gap-2">
                  <button type="button" @click="openEdit(user)" class="p-2 text-primary-600 hover:bg-primary-50 rounded transition-colors">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
                    </svg>
                  </button>
                  <button type="button" @click="deleteUser(user.id)" class="p-2 text-red-600 hover:bg-red-50 rounded transition-colors">
                    <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <polyline points="3 6 5 6 21 6"/>
                      <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div class="px-6 py-4 border-t border-slate-100 flex items-center justify-end">
        <ElPagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :current-page="page + 1"
          :page-size="pageSize"
          :page-sizes="pageSizeOptions"
          @current-change="onCurrentChange"
          @size-change="onSizeChange"
        />
      </div>

      <div v-if="!loading && users.length === 0" class="text-center py-12 text-slate-500">
        暂无数据
      </div>
      <div v-if="loading" class="text-center py-12 text-slate-400">
        加载中...
      </div>
    </div>

    <!-- Edit Modal -->
    <Teleport to="body">
      <div v-if="editOpen" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" @click.self="editOpen = false">
        <div class="bg-white rounded-2xl w-full max-w-md overflow-hidden">
          <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
            <h3 class="text-lg font-semibold text-slate-900">编辑用户</h3>
            <button type="button" @click="editOpen = false" class="text-slate-400 hover:text-slate-600">
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
            </button>
          </div>
          <div class="p-6 space-y-4">
            <div>
              <label class="block text-sm text-slate-600 mb-1">用户名</label>
              <input :value="editUserRow?.username" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm bg-slate-50" readonly>
            </div>
            <div>
              <label class="block text-sm text-slate-600 mb-1">角色</label>
              <select v-model="editForm.role" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm">
                <option value="USER">用户</option>
                <option value="ADMIN">管理员</option>
              </select>
            </div>
            <div>
              <label class="block text-sm text-slate-600 mb-1">Office ID（可选）</label>
              <input v-model="editForm.officeId" class="w-full px-3 py-2 border border-slate-200 rounded-lg text-sm" placeholder="组织/部门标识">
            </div>
          </div>
          <div class="px-6 py-4 border-t border-slate-100 flex justify-end gap-3">
            <button type="button" @click="editOpen = false" class="px-4 py-2 text-sm rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200">取消</button>
            <button type="button" @click="submitEdit" class="px-4 py-2 text-sm rounded-lg bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-50" :disabled="editSaving">
              {{ editSaving ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElPagination } from 'element-plus'
import type { User } from '@/types'
import { adminUsersApi } from '@/api/admin.users.api'

const users = ref<User[]>([])
const searchQuery = ref('')
const loading = ref(false)
const page = ref(0)
const pageSize = ref(10)
const total = ref(0)
const pageSizeOptions = [10, 20, 50, 100]

const editOpen = ref(false)
const editUserRow = ref<User | null>(null)
const editForm = ref({ role: 'USER', officeId: '' })
const editSaving = ref(false)

function isAdmin(u: User) {
  return (u.role || '').toLowerCase() === 'admin'
}

function formatDate(value: string) {
  if (!value) return '—'
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return value
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

const onCurrentChange = (p: number) => {
  page.value = Math.max(0, p - 1)
  loadUsers()
}

const onSizeChange = (s: number) => {
  pageSize.value = s
  page.value = 0
  loadUsers()
}

async function loadUsers() {
  loading.value = true
  try {
    const res = await adminUsersApi.getUsersPage({
      page: page.value,
      size: pageSize.value,
      keyword: searchQuery.value.trim() || undefined
    })
    users.value = (res.content || []) as User[]
    total.value = res.total || 0
  } catch (e) {
    console.error('加载用户列表失败:', e)
    users.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function openEdit(user: User) {
  editUserRow.value = user
  editForm.value = {
    role: (user.role || 'USER').toUpperCase() === 'ADMIN' ? 'ADMIN' : 'USER',
    officeId: user.officeId || ''
  }
  editOpen.value = true
}

async function submitEdit() {
  if (!editUserRow.value) return
  editSaving.value = true
  try {
    await adminUsersApi.updateUser(editUserRow.value.id, {
      role: editForm.value.role,
      officeId: editForm.value.officeId || undefined
    })
    ElMessage.success('已更新')
    editOpen.value = false
    await loadUsers()
  } catch (e) {
    console.error('更新用户失败:', e)
    ElMessage.error('更新失败')
  } finally {
    editSaving.value = false
  }
}

async function deleteUser(id: number) {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await adminUsersApi.remove(id)
    ElMessage.success('已删除')
    await loadUsers()
  } catch (e) {
    console.error('删除用户失败:', e)
    ElMessage.error('删除失败')
  }
}

watch(searchQuery, () => {
  page.value = 0
})

onMounted(() => {
  loadUsers()
})
</script>

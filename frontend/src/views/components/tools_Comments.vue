<template>
  <CommCommentList
    v-if="entityId"
    :entity-type="'tool'"
    :entity-id="entityId"
    ref="commentListRef"
  >
    <template #form>
      <CommCommentForm
        :entity-type="'tool'"
        :entity-id="entityId"
        @success="handleSuccess"
        @error="handleError"
      />
    </template>
  </CommCommentList>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CommCommentList from '@/views/components/comm_CommentList.vue'
import CommCommentForm from '@/views/components/comm_CommentForm.vue'

defineProps<{
  entityId: number | null | undefined
}>()

const emit = defineEmits<{
  (e: 'success'): void
  (e: 'error', message: string): void
}>()

const commentListRef = ref<InstanceType<typeof CommCommentList> | null>(null)

const handleSuccess = () => {
  emit('success')
  commentListRef.value?.refresh()
}

const handleError = (message: string) => {
  emit('error', message)
}
</script>


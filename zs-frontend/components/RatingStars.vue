<script setup lang="ts">
const props = withDefaults(defineProps<{
  modelValue?: number
  readonly?: boolean
}>(), {
  readonly: true,
  modelValue: 0,
})

const emit = defineEmits<{
  'update:modelValue': [value: number]
}>()

function setRating(n: number) {
  if (!props.readonly) {
    emit('update:modelValue', n)
  }
}
</script>

<template>
  <span class="inline-flex gap-0.5">
    <button
      v-for="n in 5" :key="n"
      type="button"
      :disabled="readonly"
      class="text-lg transition-colors"
      :class="[
        n <= modelValue ? 'text-[#f9a825]' : 'text-gray-300',
        readonly ? 'cursor-default' : 'cursor-pointer hover:scale-110'
      ]"
      @click="setRating(n)"
    >
      ★
    </button>
  </span>
</template>

<template>
  <div class="dp-wrapper">
    <div class="dp-input" @click="open = !open">
      <span class="dp-icon">📅</span>
      <span>{{ displayDate }}</span>
      <span class="dp-arrow">▼</span>
    </div>
    <div v-if="open" class="dp-dropdown">
      <div class="dp-header">
        <button @click="prevMonth">‹</button>
        <span>{{ year }}年{{ month }}月</span>
        <button @click="nextMonth">›</button>
      </div>
      <div class="dp-weekdays">
        <span v-for="d in weekDays" :key="d">{{ d }}</span>
      </div>
      <div class="dp-grid">
        <button v-for="(day, i) in days" :key="i"
          :class="['dp-day', {
            selected: day && day === selectedDay && viewMonth === selectedMonth && viewYear === selectedYear,
            today: day && day === today.getDate() && viewMonth === today.getMonth()+1 && viewYear === today.getFullYear(),
            other: !day
          }]"
          @click="day && selectDay(day)"
          :disabled="day && isFuture(day)"
        >{{ day || '' }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({ modelValue: String })
const emit = defineEmits(['update:modelValue'])

const open = ref(false)
const today = new Date()
const viewDate = ref(new Date())
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

const selectedDate = computed(() => props.modelValue ? new Date(props.modelValue + 'T00:00:00') : today)
const selectedDay = computed(() => selectedDate.value.getDate())
const selectedMonth = computed(() => selectedDate.value.getMonth() + 1)
const selectedYear = computed(() => selectedDate.value.getFullYear())

const year = computed(() => viewDate.value.getFullYear())
const month = computed(() => viewDate.value.getMonth() + 1)
const viewMonth = computed(() => viewDate.value.getMonth() + 1)
const viewYear = computed(() => viewDate.value.getFullYear())

const displayDate = computed(() => {
  const d = selectedDate.value
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
})

const days = computed(() => {
  const y = viewDate.value.getFullYear(), m = viewDate.value.getMonth()
  const firstDay = new Date(y, m, 1).getDay()
  const lastDate = new Date(y, m+1, 0).getDate()
  const result = []
  for (let i = 0; i < firstDay; i++) result.push(null)
  for (let d = 1; d <= lastDate; d++) result.push(d)
  return result
})

function prevMonth() { viewDate.value = new Date(year.value, viewDate.value.getMonth() - 1, 1) }
function nextMonth() { viewDate.value = new Date(year.value, viewDate.value.getMonth() + 1, 1) }
function isFuture(d) { const t = new Date(year.value, viewDate.value.getMonth(), d); return t > today }
function selectDay(d) {
  const s = `${year.value}-${String(month.value).padStart(2,'0')}-${String(d).padStart(2,'0')}`
  emit('update:modelValue', s)
  open.value = false
}
</script>

<style scoped>
.dp-wrapper { position: relative; }
.dp-input {
  display: flex; align-items: center; gap: 8px;
  padding: 14px 16px; border: 2px solid var(--card); border-radius: var(--radius);
  cursor: pointer; font-size: 15px; background: var(--white); color: var(--text);
  transition: border-color .2s;
}
.dp-input:hover { border-color: var(--primary-light); }
.dp-icon { font-size: 18px; }
.dp-arrow { margin-left: auto; font-size: 10px; color: var(--text2); }
.dp-dropdown {
  position: absolute; top: 100%; left: 0; right: 0; z-index: 300;
  margin-top: 6px; background: var(--white); border-radius: 14px;
  padding: 16px; box-shadow: 0 8px 32px rgba(0,0,0,0.15);
  animation: dpIn .2s ease;
}
@keyframes dpIn { from { opacity: 0; transform: translateY(-6px); } to { opacity: 1; transform: translateY(0); } }
.dp-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 12px; font-weight: 700; font-size: 15px; color: var(--text);
}
.dp-header button {
  width: 32px; height: 32px; border: none; border-radius: 50%;
  background: var(--card); cursor: pointer; font-size: 18px; color: var(--text);
  display: flex; align-items: center; justify-content: center;
}
.dp-header button:hover { background: var(--primary-light); }
.dp-weekdays { display: grid; grid-template-columns: repeat(7,1fr); text-align: center; font-size: 12px; color: var(--text2); margin-bottom: 6px; }
.dp-grid { display: grid; grid-template-columns: repeat(7,1fr); gap: 3px; }
.dp-day {
  aspect-ratio: 1; border: none; border-radius: 50%; cursor: pointer;
  font-size: 14px; color: var(--text); background: transparent;
  display: flex; align-items: center; justify-content: center;
}
.dp-day:hover { background: var(--primary-light); }
.dp-day.selected { background: var(--primary); color: #fff; font-weight: 700; }
.dp-day.today { font-weight: 700; color: var(--primary-dark); }
.dp-day.today.selected { color: #fff; }
.dp-day.other { pointer-events: none; }
.dp-day:disabled { opacity: 0.3; cursor: not-allowed; }
</style>

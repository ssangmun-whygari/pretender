<template>
  <div ref="container" class="tooltip-container">
    <Bar v-if="loaded" :data="chartData" :options="chartOptions"/>
    <div v-show="tooltipRef.show" :class="mdAndUp ? 'tooltip' : 'tooltip tooltip-small'" ref="tooltipElementRef">
      <div class="d-flex flex-column align-center" v-if="loaded && tooltipRef.title.length === tooltipRef.body.length" v-for="(title, idx) in tooltipRef.title">
        <div>{{ title }}</div>
        <div v-if="voteData && props.cast_info" class="d-flex justify-center">
          <img 
              :src="getProfileImagePath(props.cast_info.find(e => e.id === voteData[tooltipRef.dataIndex[idx]].character_id )?.profile_path)"
              class="tootip-profile-image"
              v-show="mdAndUp">
          </img>
        </div>
        <div v-for="line in tooltipRef.body[idx].lines">{{ line }}</div>
      </div>
    </div>
  </div>
</template>

<style>
  .tooltip-container {
    position: relative;
    height: var(--container-height);
  }

  .tooltip {
    background: rgba(0, 0, 0, 0.7);
    width: 10cqw;
    padding: 5px;
    border-radius: 5px;
    font-size: clamp(12px, 1cqw, 16px);
    position: absolute;
    transform: translate(-50%, 0);
    pointer-events: none;
    left: var(--tooltipLeft);
    top: var(--tooltipTop);
    color: white;
    opacity: 1;
  }

  .tooltip-small {
    width: 20cqw;
  }

  .tootip-profile-image {
    width: 50%;
    aspect-ratio: 1 / 1;
    clip-path: circle(50% at 50% 50%);
    object-fit: cover;
  }
</style>

<script setup>
  import axios from 'axios'
  import { Bar } from 'vue-chartjs'
  // https://www.chartjs.org/docs/latest/getting-started/integration.html
  import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'
  import { ref, onMounted, watch } from 'vue'
  import { useDisplay } from 'vuetify'
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const profileBaseUrl = "http://image.tmdb.org/t/p/w185"
  const props = defineProps({
    media_id: String,
    media_type: String,
    cast_info: {
      type: Array,
      default: () => {return null}
    }
  })
  const voteData = ref(null)
  const container = ref(null)
  const loaded = ref(false)
  const tooltipRef = ref({
    show: false,
    x: 0,
    y: 0,
    title: [],
    body: [],
    dataIndex: []
  })
  const tooltipElementRef = ref(null)
  let chartData = null
  const chartOptions = {
    reaponsive: true,
    maintainAspectRatio: false,
    plugins: {
      tooltip: {
        enabled: false,
        position: 'nearest',
        external: externalTooltipHandler
      }
    }
  }
  const { smAndDown, mdAndUp, lgAndUp, width } = useDisplay();

  function externalTooltipHandler(context) {
    const {chart, tooltip} = context;
    console.log("externalTooltipHandler 호출")
    console.log(chart)
    console.log(tooltip)
    // tooltip.title의 예 : ['Amy Santiago']
    // tooltip.body의 예 : ['득표 수 : 47']

    if (!tooltip || tooltip.opacity === 0) {
      tooltipRef.value.show = false;
      return
    }

    const containerRect = container.value.getBoundingClientRect()
    const canvasRect = chart.canvas.getBoundingClientRect()
    const x = Math.ceil((canvasRect.left - containerRect.left) + tooltip.caretX)
    const y = Math.ceil((canvasRect.top - containerRect.top) + tooltip.caretY)
    console.log("x : " + x + ", y : " + y);

    tooltipRef.value = {
      show: true,
      x: x,
      y: y,
      title: tooltip.title,
      body: tooltip.body,
      dataIndex: tooltip.dataPoints.map(e => e.dataIndex)
    }
  }

  async function fetchVoteData(media_id, media_type) {
    try {
      const response = await axios.get(
        apiBaseUrl + '/api/detail/castVotes', // url
        {
          params: {
            mediaId: media_id, 
            type: media_type
          }
        }
      )
      return response
    } catch (error) {
      console.error("투표 정보를 불러오는 데 실패했습니다.")
      console.error(error)
      return null
    }
  }

  function resizeVoteGraph() {
    if (lgAndUp.value) {
      container.value?.style.setProperty('--container-height', '640px')
      return
    }
    if (mdAndUp.value) {
      container.value?.style.setProperty('--container-height', '480px')
      return
    }
    if (smAndDown.value) {
      container.value?.style.setProperty('--container-height', '320px')
      return
    }
  }

  function getProfileImagePath(path) {
    if (path) return profileBaseUrl + path
    else return apiBaseUrl + '/resource/characterVoteNoImage'
  }

  watch(tooltipRef, () => {
    tooltipElementRef.value?.style.setProperty('--tooltipLeft', `${tooltipRef.value.x}px`);
    tooltipElementRef.value?.style.setProperty('--tooltipTop', `${tooltipRef.value.y}px`);
  })

  watch(width, () => {
    resizeVoteGraph()
  })

  onMounted(async () => {
    loaded.value = false;
    const response = await fetchVoteData(props.media_id, props.media_type)
    voteData.value = response.data;
    chartData = {
      labels: response.data.map((entry) => entry.character_name),
      datasets: [
        {
          label: "득표 수",
          backgroundColor: '#f87979',
          data: response.data.map((entry) => entry.votes)
        }
      ]
    }
    resizeVoteGraph()
    loaded.value = true;
  })

  ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)
</script>

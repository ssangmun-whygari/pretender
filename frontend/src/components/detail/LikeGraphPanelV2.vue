<template>
  <!-- <p>{{ JSON.stringify(props.vote) }}</p> -->
  <div v-if="props.vote.likeGraphPanelReady && !props.vote.apiSuccess">
    <div class="d-flex justify-center"><div style="font-size: 120px;">📊</div></div>
    <div class="d-flex justify-center"><h3>투표 결과를 불러오는 데 실패했습니다.</h3></div>
  </div>
  <div v-if="props.vote.likeGraphPanelReady && props.vote.apiSuccess && !props.vote.anyVoteExists">
    <div class="d-flex justify-center"><div style="font-size: 120px;">📊</div></div>
    <div class="d-flex justify-center"><h3>아직 투표한 사람이 없습니다.</h3></div>
  </div>
  <div v-if="props.vote.likeGraphPanelReady && props.vote.apiSuccess && props.vote.anyVoteExists && chartDataReady">
    <h3>인기순위</h3>
    <div ref="container" class="tooltip-container">
      <Bar :data="chartData" :options="chartOptions"/>
      <div v-show="tooltipRef.show" :class="mdAndUp ? 'tooltip' : 'tooltip tooltip-small'" ref="tooltipElementRef">
        <div class="d-flex flex-column align-center" v-if="tooltipRef.title.length === tooltipRef.body.length" v-for="(title, idx) in tooltipRef.title">
          <div>{{ title }}</div>
          <div v-if="props.cast_info" class="d-flex justify-center">
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
  import { Bar } from 'vue-chartjs'
  // https://www.chartjs.org/docs/latest/getting-started/integration.html
  import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'
  import { ref, computed, onMounted, watch, toRefs } from 'vue'
  import { useDisplay } from 'vuetify'
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const profileBaseUrl = "http://image.tmdb.org/t/p/w185"
  const props = defineProps({
    cast_info: {
      type: Array,
      default: () => {return null}
    },
    vote: {
      type: Object,
      default: () => {return {
        likeGraphPanelReady: false,
        apiSuccess: false,
        anyVoteExists: false,
        voteData: null
      }}
    }
  })
  const voteData = computed(() => props.vote.voteData ?? [])
  const container = ref(null) // HTML Element 직접 조작
  const tooltipElementRef = ref(null)
  const tooltipRef = ref({
    show: false,
    x: 0,
    y: 0,
    title: [],
    body: [],
    dataIndex: []
  })
  const chartData = ref(null) // Bar 컴포넌트에서 차트를 렌더링하는 데 쓰임.
  const chartOptions = {
    maintainAspectRatio: false,
    plugins: {
      tooltip: {
        enabled: false,
        position: 'nearest',
        external: externalTooltipHandler
      }
    }
  }
  const chartDataReady = ref(false)
  const { smAndDown, mdAndUp, lgAndUp, width } = useDisplay();

  function externalTooltipHandler(context) {
    const {chart, tooltip} = context;
    // console.log("externalTooltipHandler 호출")
    // console.log(chart)
    // console.log(tooltip)
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

    tooltipRef.value = {
      show: true,
      x: x,
      y: y,
      title: tooltip.title,
      body: tooltip.body,
      dataIndex: tooltip.dataPoints.map(e => e.dataIndex)
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
    resizeVoteGraph()
  })

  watch(() => {return { castInfo: props.cast_info, vote: props.vote }}, 
    (newVal) => {
      // console.log("castInfo", newVal.castInfo);
      // console.log("vote", newVal.vote);
      let castInfo = newVal.castInfo;
      let vote = newVal.vote;
      if (castInfo.length === 0) return;
      if (!vote.voteData) return;
      // chartData를 만든다.
      let labels = vote.voteData.map((v) => {
        let i = castInfo.findIndex((c) => c.id === v.character_id);
        return (i === -1) ? 'Unknown' : castInfo[i].character;
      });
      // console.log("labels", labels)
      chartData.value = {
        labels: labels,
        datasets: [{
            label: "득표 수",
            backgroundColor: '#36A2EB',
            data: vote.voteData.map(v => v.votes)
        }]
      };
      chartDataReady.value = true;
    }
  )

  ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)
</script>

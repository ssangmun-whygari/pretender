<template>
  <v-dialog v-model="dialog" max-width="960">
    <v-sheet class="pa-3">
      <h1>캐릭터 통계</h1>
      <div class="d-flex justify-center">
        <div v-if="loaded && apiSuccess && voteExists" class="pie-container">
          <Pie :data="chartData"/>
        </div>
      </div>
      <div v-if="loaded && apiSuccess && !voteExists">
        <div class="d-flex justify-center"><div style="font-size: 120px;">😢</div></div>
        <div class="d-flex justify-center"><h3>이 캐릭터에게 투표한 사람이 아직 없습니다.</h3></div>
      </div>
      <div v-if="loaded && !apiSuccess">
        <div class="d-flex justify-center"><div style="font-size: 120px; filter: grayscale(100%);">📥</div>
        <div class="d-flex justify-center"></div><h3>데이터를 불러오는 데 실패했습니다.</h3></div>
      </div>
    </v-sheet>
  </v-dialog>
</template>

<style>
  .pie-container {
    width: 80%;
    aspect-ratio: 1 / 1;
  }
</style>

<script setup>
  import { ref, watch, computed, onMounted } from 'vue'
  import axios from 'axios'
  import { Pie } from 'vue-chartjs'
  import { Chart as ChartJS, Tooltip, PieController, ArcElement } from 'chart.js'
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const emit = defineEmits(["update:voteChartDialog"])
  const props = defineProps({
    voteChartDialog: {
      type: Object,
      default: () => ({ character_id: null, show: false }),
    },
    media_id: String,
    media_type: String
  })
  const dialog = computed({
    get: () => props.voteChartDialog.show,
    set: (v) => emit("update:voteChartDialog", {character_id: props.voteChartDialog.character_id, show: v})
  })
  const loaded = ref(false); // chartData의 값이 불러와졌는지
  const apiSuccess = ref(false);
  const chartData = ref(null);
  const voteExists = ref(false); // 그 캐릭터에 대한 표가 존재하는지
  
  async function fetchVoteReasonsOfCharacter() {
   try {
     const response = await axios.get(apiBaseUrl + "/api/detail/votesReasons",{
       params: { 
        mediaId: props.media_id, 
        type: props.media_type, 
        characterId: props.voteChartDialog.character_id},
     })
     console.log("response.data : ===================================================");
     console.log(response.data);
     return response.data;
   } catch (error) {
     console.error("캐릭터별 투표 이유 API 호출 중 오류 발생 : ", error);
     return null;
   }
  }

  watch(dialog, async (isOpen) => {
    if (isOpen) {
      const sourceData = await fetchVoteReasonsOfCharacter();
      if (!sourceData) { // null => API 실패
        loaded.value = true
        return
      }
      apiSuccess.value = true;
      if (sourceData.length == 0) {
        voteExists.value = false
        loaded.value = true
        return
      }
      chartData.value = {
        labels: sourceData.map(entry => entry.content),
        datasets: [{
          label: "득표 수",
          backgroundColor: [
            'rgba(255, 99, 132, 0.5)',
            'rgba(130, 192, 192, 0.5)', //mint
            'rgba(255, 159, 64, 0.5)',
            'rgba(255, 205, 86, 0.5)',
            'rgba(170, 192, 100, 0.5)', //lightgreen
            'rgba(45, 162, 235, 0.5)', // blue 
            'rgba(153, 102, 255, 0.5)',
            'rgba(201, 203, 207, 0.5)'
          ],
          data: sourceData.map((entry) => entry.votes)
        }]
      }
      voteExists.value = true
      loaded.value = true
    } // isOepn end
  }) // watch end

  ChartJS.register(Tooltip, PieController, ArcElement);
</script>
<template>
  <h2>캐릭터 통계</h2>
  <canvas ref="doughnutChart"></canvas>
</template>

<script setup>
import axios from 'axios';
import { onMounted,ref } from 'vue';
import Chart from 'chart.js/auto';

const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL

  const props = defineProps({
    media_id: String,
    media_type: String,
    character_id: Number,
  });

  const doughnutChart = ref(null);
  let myChart = null; // 기존 차트 저장
  const voteReasonsData = ref([]);

  async function fetchVoteReasons() {
    console.log("fetchVoteReasons...");
   try {
     const response = await axios.get(apiBaseUrl + "/api/detail/votesReasons",{
       params: { mediaId: props.media_id, type: props.media_type, characterId: props.character_id},
     })
     return response.data;
   } catch (error) {
     console.error("fetchingVoteReasons 오류 발생! : "+ error);
     return [];
   }
  } 

  async function showChart() {
    console.log("showChart...");
    voteReasonsData.value = await fetchVoteReasons();
    console.log(voteReasonsData.value);

    renderChart();
  }

  function renderChart() {
      if (!doughnutChart.value) {
      console.error("차트를 찾을 수 없습니다.");
      return;
    }

    // 기존 차트(있다면) 제거
    if (myChart) {
      myChart.destroy();
    }

    let labels, dataValues, backgroundColors;

    if (voteReasonsData.value.length === 0) {
      labels = ["득표 없음"];
      dataValues = [100];
      backgroundColors = ['rgba(200, 200, 200, 0.5)']; 
    } else {
      labels = voteReasonsData.value.map(item => item.content);
      dataValues = voteReasonsData.value.map(item => item.votes);
      backgroundColors = [
        'rgba(255, 99, 132, 0.5)',
        'rgba(130, 192, 192, 0.5)', //mint
        'rgba(255, 159, 64, 0.5)',
        'rgba(255, 205, 86, 0.5)',
        'rgba(170, 192, 100, 0.5)', //lightgreen
        'rgba(45, 162, 235, 0.5)', // blue 
        'rgba(153, 102, 255, 0.5)',
        'rgba(201, 203, 207, 0.5)'
        ];
    }

    const data = {
      labels: labels,
      datasets: [{
        label: '투표수',
        data: dataValues,
        backgroundColor: backgroundColors,
        hoverOffset: 4
      }]
    };

    const config = {
      type: 'doughnut',
      data: data,
    };

    myChart = new Chart(doughnutChart.value, config);
  }
    
  onMounted(async () => {
    showChart();
  })
</script>
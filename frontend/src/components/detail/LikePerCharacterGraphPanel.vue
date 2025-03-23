<template>
  <h2>캐릭터 통계</h2>
  <canvas ref="doughnutChart"></canvas>
</template>

<script setup>
import axios from 'axios';
import { onMounted,ref } from 'vue';
import Chart, { plugins } from 'chart.js/auto';
import ChartDataLabels from 'chartjs-plugin-datalabels';

  const props = defineProps({
    media_id: String,
    media_type: String,
    character_id: Number,
  });

  Chart.register(ChartDataLabels);

  const doughnutChart = ref(null);
  let myChart = null; // 기존 차트 저장
  const voteReasonsData = ref([]);

  async function fetchVoteReasons() {
   try {
     const response = await axios.get("http://localhost:8080/api/detail/votesReasons",{
       params: { mediaId: props.media_id, type: props.media_type, characterId: props.character_id},
     })
     return response.data;
   } catch (error) {
     console.error("fetchingVoteReasons 오류 발생! : "+ error);
     return [];
   }
  } 

  async function showChart() {
    voteReasonsData.value = await fetchVoteReasons();
    console.log(voteReasonsData.value);

    renderChart();
  }

  function renderChart() {
      if (!doughnutChart.value) {
      console.error("차트를 찾을 수 없습니다.");
      return;
    }

    // 원래 있는 차트 제거
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
      options: {
        plugins: {
          legend: {
            labels: {
              font: {
                size: 18
              }
            }
          },
          tooltip: {
            bodyFont: {
              size: 16
            },
            position: 'nearest',
            yAlign: 'bottom', // 툴팁을 살짝 위로 올리기
            caretPadding: 13, //툴팁 더 올리기(툴팁과 차트의 간격)
            callbacks: {
              label: function (tooltipItem) {
                let label = tooltipItem.label;
                if(label === "득표 없음") {
                  return "0표";
                }
                return `${tooltipItem.raw}표`;
              }
            }
          },
          datalabels: {
            color: '#777',
            font: {
              size: 14,
              weight: 'bold'
            },
            anchor: 'center', //가운데 위치
            align: 'center',
            formatter: (value, ctx) => {
              let label = ctx.chart.data.labels[ctx.dataIndex];
              if (label === "득표 없음") {
                return ""; 
              }
              let words = label.split(" ");
              let wordsWithEnter = words.join("\n");
              return wordsWithEnter;
            }
          }
        }
      }
    };

    myChart = new Chart(doughnutChart.value, config);
  }
    
  onMounted(async () => {
    showChart();
  })
</script>
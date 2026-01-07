<template>
   <div>
      <canvas ref="chartCanvas"></canvas>
   </div>
</template>

<script setup>
  import {ref, onMounted, onUnmounted, watchEffect} from "Vue";
  import Chart from "chart.js/auto";
  import axios from "axios";
  const apiBaseUrl = import.meta.env.VITE_APP_API_BASE_URL
  const props = defineProps({
    media_id: String,
    media_type: String,
    cast_info: Array,
  });

  const chartCanvas = ref(null);
  let myChart = null;
  const profileBaseUrl = "http://image.tmdb.org/t/p/w185"; // 기본 이미지
  const profileMap = ref({}); //객체
  const fullData =ref([]); //배열

  async function fetchVoteData() {
    try {
      const response = await axios.get(apiBaseUrl + "/api/detail/castVotes", {
        params: { mediaId: props.media_id, type: props.media_type },
      });

      return response.data; 
    } catch (error) {
    console.error("fetchingVoteData 오류 발생! : "+ error);
    return [];
    }
  }

  async function showChart() {
    const voteData = (await fetchVoteData()) || [];
 
    const voteMap = {};
    voteData.forEach((item) => {
      voteMap[String(item.character_id)] = item.votes; // String으로 형식 맞춰주기
    });

   
      setTimeout(() => {
        if (props.cast_info && props.cast_info.length > 0) {
            fullData.value = props.cast_info.map((cast) => ({
              character_id: String(cast.id),
              character_name: cast.character,
              actor_name: cast.name,
              votes: voteMap[String(cast.id)] || 0, // Votes 없으면 0으로 처리
            }));

          fullData.value.sort((a, b) => b.votes - a.votes);

          const newProfileMap = {};
          props.cast_info.forEach((cast) => {
            if (cast.profile_path) {
              newProfileMap[String(cast.id)] = `${profileBaseUrl}${cast.profile_path}`;
            }
          });

          profileMap.value = newProfileMap; 
          renderChart();
        } 
      }, 1000);

    }

    function renderChart() { 
      if(myChart) {
        myChart.destroy(); //기존차트삭제
      }
      const extraSpace = 40; //x축아래 여백 추가, 그래프 생성 전에 해야 함
      myChart = new Chart(chartCanvas.value, {
      type: "bar",
      data: {
        labels: fullData.value.map((item) => `${item.character_name}`), 
        datasets: [
          {
            label: "총 득표수",
            data: fullData.value.map((item) => item.votes),
            backgroundColor: 'rgba(54, 162, 235, 0.5)',
            borderColor: 'rgba(54, 162, 235)',
            borderWidth: 1,
            hoverBackgroundColor: 'rgba(255, 99, 132, 0.5)',
            hoverBorderColor: 'rgba(255, 99, 132)',
          },
        ],
      },
      options: {
        layout: {  // 미리 설정해야 적용됨
          padding: {
            bottom: extraSpace, 
            right: 50, // 마지막 캐릭터의 이름이 잘리지 않게 오른쪽에 여백 추가
          },
        },
        scales: {
          x: {
            ticks: {
              color: "transparent",
            },
          },
        },
      },
      plugins: [
        {
          afterDatasetsDraw(chart) {
            const ctx = chart.ctx;
            const xAxis = chart.scales.x;
            const yAxis = chart.scales.y;
            const totalBars = fullData.value.length;
            const barWidth = xAxis.width / totalBars; // 바 하나의 Width
            const maxTextWidth = barWidth * 0.5; 
            const lineHeight = 14; // 줄높이

            chart.options.layout = {
              padding: {
                bottom: extraSpace, // 차트 아래 공간
              },
            };

              // 단어를 maxWidth에 맞게 줄바꿈 처리
            function wrapWord(ctx, word, maxWidth) {
              let lines = [];
              let currentLine = "";
              for (let i = 0; i < word.length; i++) {
                const testLine = currentLine + word[i];
                if (ctx.measureText(testLine).width <= maxWidth) {
                  currentLine = testLine;
                } else {
                  lines.push(currentLine);
                  currentLine = word[i];
                }
              }
              if (currentLine !== "") lines.push(currentLine);
              return lines;
            }

            fullData.value.forEach((item, index) => {
              const characterId = item.character_id;
              const imageUrl = profileMap.value[characterId];

              if (imageUrl) {
                const img = new Image();
                img.src = imageUrl;
                img.onload = function () {
                  requestAnimationFrame(() => {
                    const maxImgSize = 35; 
                    const padding = 2; // 사진 ~ 이름

                    let imgWidth = img.width;
                    let imgHeight = img.height;
                    imgWidth = (imgWidth / imgHeight) * maxImgSize;
                    imgHeight = maxImgSize;

                    const xPos = xAxis.getPixelForTick(index) - barWidth/2.5; // 이름 왼쪽// /2를 하면 완전 딱붙음
                    const yPos = yAxis.bottom + 5; 

                    // 사진 동그라미로 만들기
                    ctx.save();// 일단 현재상태 저장
                    ctx.beginPath();
                    ctx.arc(xPos + imgWidth / 2, yPos + imgHeight / 2, Math.min(imgWidth,imgHeight)/2, 0, Math.PI * 2);
                    ctx.closePath();
                    ctx.clip();

                    ctx.drawImage(img, xPos, yPos, imgWidth, imgHeight); 
                    ctx.restore(); // 상태 복원(다른 그리기에 영향 안주도록)

                    const rawText = (item.character_name === "Self")
                                ? item.actor_name
                                : item.character_name;
                    let displayText = rawText;
                    if (ctx.measureText(rawText).width > barWidth) {
                      const words = rawText.split(" ");
                      displayText = words[0] || "";
                    }

                    // 첫단어가 maxTextWidth를 초과하면 wrap 처리
                    const wrappedLines = wrapWord(ctx, displayText, maxTextWidth);
                          
                    let textX = xPos + imgWidth + padding; // 사진의 오른쪽
                    let textY = yPos + imgHeight / 2 + 5; // 중앙 정렬

                    // 이름이 너무 길면면 줄바꿈
                 
                    let yOffset = 0;
                    wrappedLines.forEach((line) => {
                      ctx.fillText(line, textX, textY + yOffset);
                      yOffset += lineHeight;
                    });
                  });
                };
              }
            });
          },
        },
      ],
    });
  }
  function handleResize() {
  // 창크기가 변경되면 차트 리사이즈
  if (myChart) {
    myChart.resize();
  }
}

  onMounted(async () => {
    window.addEventListener("resize", handleResize); //이벤트 종류: resize
    setTimeout(() => {
      showChart(); 
    },1600 ); //조금 늦게 시작해야 페이지 애니메이션과 상관없이 차트가 움직임
  });

  onUnmounted(() => {
    window.removeEventListener("resize", handleResize);
  });
</script>

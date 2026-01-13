<template>
  <v-row justify="center">
    <v-col lg="8" cols="12">
      <div class="d-flex align-center">
        <div class="d-flex align-center" v-if="watchList.loaded == true && watchList.data.length > 0">
          <h1 class="mr-3">내가 본 작품 리스트</h1>
          <!-- TODO : 나중에 구현 <v-btn class="mr-3" color="primary">펼쳐보기</v-btn>
          <v-icon icon="mdi-cog" size="large" @click="toggleShowOptions('collection-primary')"/> -->
        </div>
      </div>
      <div v-if="watchList.loaded == true && watchList.data.length == 0">
        <h1>내가 본 작품 리스트</h1>
        <div class="empty-my-list">
          <div style="font-size: 120px;">📌</div>
          <h3>리스트에 들어있는 작품이 없습니다.</h3>
        </div>
      </div>

      <div v-if="watchList.loaded == true && watchList.data.length > 0" class="position-relative mt-3">
        <swiper-container 
          class="slider"
          :breakpoints="{
            '@0.75': {
              slidesPerView: 3,
              spaceBetween: 20,
            },
            '@1.00': {
              slidesPerView: 4,
              spaceBetween: 40,
            },
            '@1.50': {
              slidesPerView: 5,
              spaceBetween: 50,
            },
          }"
        >
          <swiper-slide 
            class="slide"
            v-for="(item, index) in watchList.data"
          >
            <div class="d-flex flex-column">
              <v-sheet class="py-3 w-100 h-100 border rounded-lg poster-image-sheet" :elevation="5">
                <RouterLink
                  class="w-100 h-100 d-flex flex-column justify-center align-center"
                  :to="{path: '/detail', query: {id : item.mediaId, type : item.mediaType}}">
                  <div class="poster-image-container">
                    <img
                      :src="posterPath(item.posterPath)"
                      class="poster mb-1"
                    ></img>
                  </div>
                </RouterLink>
              </v-sheet>
              <v-sheet class="d-flex justify-center mt-3 py-3 w-100 h-100 border rounded-lg" :elevation="2">
                <div>{{ shorten(item.mediaTitle) }}</div>
              </v-sheet>
            </div>

          </swiper-slide>
        </swiper-container>
        <div class="backdrop" id="collection-primary">
          <div class="position-relative d-flex justify-center align-center w-100 h-100">
            <div v-if="optionStates['collection-primary']">
              <v-checkbox style="color: white;" color="primary" density="compact" label="리스트 공개하기"
                v-model="optionStates['collection-primary'].options.publicList"></v-checkbox>
              <v-checkbox color="primary" density="compact" label="댓글 허용"
                v-model="optionStates['collection-primary'].options.allowReply"></v-checkbox>
            </div>
            <div class="position-absolute ma-3 top-0 right-0">
              <v-icon icon="mdi-window-close" size="large" @click="closeShowOptions('collection-primary')"/>
            </div>
          </div>
        </div>


      </div>
    </v-col>
  </v-row>
</template>

<style>
  .empty-my-list {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 100%;
    margin-top: 20px;
  }

  .poster-image-container {
    width: 70%;
    height: 100%;
    overflow: hidden;
  }

  .poster-image-container:hover .poster {
    transition: transform 0.1s linear;
    transform: scale(1.2);
  }

  .poster-image-sheet:hover {
    background-color: #EEEEEE;
  }
</style>

<script setup>
  import { computed } from 'vue'
  import { register } from 'swiper/element/bundle'
  import { ref, onMounted, watch, nextTick } from 'vue'
  
  const props = defineProps({
    watchList: Object
  })
  const posterBaseUrl = "http://image.tmdb.org/t/p/w500/";
  const noImageUrl = "https://upload.wikimedia.org/wikipedia/commons/1/14/No_Image_Available.jpg?20200913095930";
  const optionStates = ref({});

  const toggleShowOptions = (elementId) => {
    console.log("toggleShowOptions...")
    let element = document.getElementById(elementId)
    let computedStyle = window.getComputedStyle(element).display;
    if (computedStyle === "none") {
      element.style.display = "flex";
    } else {
      element.style.display = "none";
    }
  }

  const closeShowOptions = (elementId) => {
    let element = document.getElementById(elementId)
    element.style.display = "none"
  }

  const posterPath = (string) => {
    if (string) {
      return posterBaseUrl + string 
    } else {
      noImageUrl
    }
  }

  const shorten = (string) => {
    if (string.length > 12) {
      string = string.substr(0, 12)
      string += "..."
    }
    return string
  }

  // NOTE : props로 전달하면 ref로 전달해도 저절로 해제가 됨. 따라서 props.watchList.value가 아니라 props.watchList가 맞음
  watch(() => {return props.watchList.loaded}, (bool) => {
    if (bool == true) {
      (optionStates.value)["collection-primary"] = {
        "visible" : false,
        "options" : {
          "publicList" : true, // 리스트 공개하기의 기본값 : true
          "allowReply" : false, // 댓글 허용의 기본값 : false
        }
      }
      // TODO : 서버에 보내서 옵션 상태 갱신하기
      // TODO : 서버에서 갱신이 안되면 토스트로 에러 메시지 띄우기
      // allowReply 해제할 때 답글이 있으면 '지금까지 달린 답글은 비공개처리됩니다'라고 토스트 띄우기
    }
  }, {deep : true}) 
  // NOTE: props.watchList는 객체인데 내부 속성인 loaded의 변화를 감지하려면 {deep : true}를 붙여야 함
  // return props.watchList + {deep : true}를 써도 가능, 내부 속성 아무거나 바뀌어도 감지함
  // 근데 return props.watchList + {deep : false} 조합은 작동 안함. props.watchList의 포인터가 변하지 않으므로...

  // 컴포넌트가 로드될 떄 검사홤
  if (props.watchList.loaded == true) {
    (optionStates.value)["collection-primary"] = {
        "visible" : false,
        "options" : {
          "publicList" : true, // 리스트 공개하기의 기본값 : true
          "allowReply" : false, // 댓글 허용의 기본값 : false
        }
    }
  }

  register(); // swiper 등록
</script>
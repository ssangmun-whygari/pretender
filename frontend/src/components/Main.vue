<template>
    <h1 style="text-align: center;" class="mb-5">pretender</h1>
    <div>
        <v-autocomplete
            class="mx-auto main-search-bar"
            :max-width="lgAndUp ? 1000 : null"
            label="제목을 검색하세요"
            v-on:keyup.enter="onEnter"
            :items="[]"
            v-model="query"
        ></v-autocomplete>
    </div>
    <p>{{ responseData }}</p>
</template>

<script setup>
    import { useDisplay } from 'vuetify';
    import axios from 'axios'
    import { ref } from 'vue'
    const { lgAndUp } = useDisplay();
    const query = defineModel()
    let responseData = ref(null)

    // axios 요청
    async function onEnter() {
        responseData = await axios.get(
            'http://localhost:8080/', {
                params: {
                    query: query.value
                }
            }).then(function (response) {
                responseData.value = response.data
                console.log(response)
            })
    }
</script>
<template>
    <div>
        <canvas ref="chartCanvas" class="chart-canvas"></canvas>
        <div v-if="visualizationData !== null">
            <div class="value">
                <div class="row1">
                    <div>활동성 : {{ visualizationData.activity.toFixed(4) }}</div>
                    <div>안정성 : {{ visualizationData.stability.toFixed(4) }}</div> 
                    <div>효율성 : {{ visualizationData.efficiency.toFixed(4) }}</div>
                </div>
                <div class="row2">
                    <p>성장성 : {{ visualizationData.growth.toFixed(4) }}</p> 
                    <p>수익성 : {{ visualizationData.profitability.toFixed(4) }}</p>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Chart from 'chart.js/auto';
import 'chartjs-adapter-date-fns';
import 'chartjs-plugin-datalabels';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import { useRoute } from 'vue-router';
import { getCompanyDetail } from '@/apis/api/company';


const chartCanvas = ref(null);
const visualizationData = ref(null);
const accessToken = localStorage.getItem('accessToken');
const store = useCounterStore();
const route = useRoute();
const companyId = ref(route.params.companyId);
const API_URL = store.API_URL;

const getcompanyInformation = function () {
    getCompanyDetail(
        companyId.value,
        (res) => {
            visualizationData.value = res.data.data.factor;
            console.log(visualizationData.value);
            // console.log('데이터 받음');
            renderChart(); // 데이터를 받은 후에 차트를 렌더링
        },
        (res) => {
            console.log(err);
        }
    );
};

const renderChart = () => {
    const ctx = chartCanvas.value.getContext('2d');
    const dataValues = Object.values(visualizationData.value);
    const maxDataValue = Math.max(...dataValues); // 데이터의 최대값 계산
    const normalizedData = dataValues.map((value) => Math.min(value, 9)); // 최대값을 9로 제한

    new Chart(ctx, {
        type: 'radar',
        data: {
            labels: ['안정성', '수익성', '활동성', '성장성', '효율성'], // 수정된 부분: 영어에서 한글로 변경
            datasets: [
                {
                    label: '점수',
                    data: normalizedData,
                    backgroundColor: 'rgba(0, 123, 255, 0.7)',
                    borderColor: 'rgba(0, 123, 255, 1)',
                    borderWidth: 1,
                },
            ],
        },
        options: {
            scales: {
                r: {
                    max: 9, // 최대 값 설정
                    min: -6, // 최소 값 설정
                    stepSize: 3,
                },
            },
            plugins: {
                legend: {
                    position: 'top',
                },
                datalabels: {
                    display: true, // 데이터 라벨 표시 여부
                    color: '#000',
                    font: {
                        weight: 'bold',
                    },
                },
            },
        },
    });
};

onMounted(() => {
    getcompanyInformation();
});
</script>

<style scoped>
.chart-canvas {
    width: 25rem; /* 원하는 너비로 설정 */
    height: 20rem; /* 원하는 높이로 설정 */
}
/* .value{
    display: flex;
} */
.row1{
    display: flex;
    justify-content: space-around;
}
.row2{
    display: flex;
    justify-content: space-around;
    margin-top: 0.6em;
}
</style>

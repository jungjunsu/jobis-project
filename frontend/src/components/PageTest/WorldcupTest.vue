<template>
    <div class="container">
        <div>
            <button @click="startTournament" v-if="!isTournamentStarted">대진 시작</button>
            <button @click="resetTournament" v-if="winner">다시하기</button>
            <!-- 다시하기 버튼 추가 -->
            <div v-if="!winner">
                <!-- 페이드 애니메이션을 적용한 라운드 표시 -->
                <transition :name="isNextRound ? 'fade-next-round' : 'fade'" mode="out-in">
                    <h2 style="text-align: center" :key="currentRound">
                        {{ currentRound }}
                    </h2>
                </transition>
                <transition :name="isNextRound ? 'fade-next-round' : 'fade'" mode="out-in">
                    <div
                        v-if="currentMatchIndex < matches.length"
                        class="current-match-display"
                        :key="currentMatchIndex">
                        <!-- 기업 1 카드 -->
                        <div class="card" @click="selectWinner(currentMatchIndex, 0)">
                            <!-- 시각화 영역 -->
                            <div class="image">
                                <canvas class="chart-canvas" ref="chartCanvas1"></canvas>
                            </div>
                            <!-- 기업 정보 표시 -->
                            <div class="text">
                                <span>{{ matches[currentMatchIndex][0].name }}</span>
                                <!-- 기업의 특성 값들을 반복문을 통해 표시 -->
                                <div
                                    v-for="(value, key) in matches[currentMatchIndex][0]
                                        .visualizationData"
                                    :key="key">
                                    <p>{{ key }} : {{ value }}</p>
                                </div>
                                <br />
                                <span>전체 분석 내용</span>
                                <p style="width: 380px">
                                    이 기업은 특허 개수를 바탕으로 성장성이 뛰어나고 어쩌구저쩌구
                                </p>
                            </div>
                        </div>
                        <!-- 기업 2 카드 -->
                        <div class="card" @click="selectWinner(currentMatchIndex, 1)">
                            <!-- 시각화 영역 -->
                            <div class="image">
                                <canvas class="chart-canvas" ref="chartCanvas2"></canvas>
                            </div>
                            <!-- 기업 정보 표시 -->
                            <div class="text">
                                <span>{{ matches[currentMatchIndex][1].name }}</span>
                                <!-- 기업의 특성 값들을 반복문을 통해 표시 -->
                                <div
                                    v-for="(value, key) in matches[currentMatchIndex][1]
                                        .visualizationData"
                                    :key="key">
                                    <p>{{ key }} : {{ value }}</p>
                                </div>
                                <br />
                                <span>전체 분석 내용</span>
                                <p style="width: 380px">
                                    이 기업은 특허 개수를 바탕으로 성장성이 뛰어나고 어쩌구저쩌구
                                </p>
                            </div>
                        </div>
                    </div>
                </transition>
            </div>
            <div v-else>
                <h2>축하합니다!</h2>
                <p>{{ winner.name }}가 코드 선택 월드컵에서 우승했습니다!</p>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import axios from 'axios';
import { useCounterStore } from '@/stores/counter';
import Chart from 'chart.js/auto';
import 'chartjs-adapter-date-fns';
import 'chartjs-plugin-datalabels';

const store = useCounterStore();
onMounted(() => {
    createMatches();
});
const isNextRound = ref(false);
const companies = [
    {
        name: '기업1',
        visualizationData: {
            안정성: 9,
            성장성: -9,
            활동성: 4,
            수익성: -2,
            가용성: 5,
        },
    },
    {
        name: '기업2',
        visualizationData: {
            안정성: 1,
            성장성: -2,
            활동성: 3,
            수익성: -4,
            가용성: 5,
        },
    },
    {
        name: '기업3',
        visualizationData: {
            안정성: 1,
            성장성: -6,
            활동성: 6,
            수익성: 1,
            가용성: -5,
        },
    },
    {
        name: '기업4',
        visualizationData: {
            안정성: -9,
            성장성: -1,
            활동성: 5,
            수익성: -5,
            가용성: 1,
        },
    },
    {
        name: '기업5',
        visualizationData: {
            안정성: 4,
            성장성: -3,
            활동성: 1,
            수익성: -3,
            가용성: 4,
        },
    },
    {
        name: '기업6',
        visualizationData: {
            안정성: 6,
            성장성: -5,
            활동성: 3,
            수익성: -1,
            가용성: 4,
        },
    },
    {
        name: '기업7',
        visualizationData: {
            안정성: 5,
            성장성: -2,
            활동성: 3,
            수익성: -7,
            가용성: 1,
        },
    },
    {
        name: '기업8',
        visualizationData: {
            안정성: 7,
            성장성: -4,
            활동성: 3,
            수익성: -1,
            가용성: 4,
        },
    },
    {
        name: '기업9',
        visualizationData: {
            안정성: 8,
            성장성: -1,
            활동성: 3,
            수익성: 0,
            가용성: 2,
        },
    },
    {
        name: '기업10',
        visualizationData: {
            안정성: 9,
            성장성: 9,
            활동성: 1,
            수익성: -7,
            가용성: -4,
        },
    },
    {
        name: '기업11',
        visualizationData: {
            안정성: 9,
            성장성: 0,
            활동성: 7,
            수익성: 2,
            가용성: -5,
        },
    },
    {
        name: '기업12',
        visualizationData: {
            안정성: 5,
            성장성: -3,
            활동성: 1,
            수익성: -3,
            가용성: 0,
        },
    },
    {
        name: '기업13',
        visualizationData: {
            안정성: 1,
            성장성: -3,
            활동성: 5,
            수익성: 7,
            가용성: 8,
        },
    },
    {
        name: '기업14',
        visualizationData: {
            안정성: 3,
            성장성: -2,
            활동성: 5,
            수익성: -6,
            가용성: 2,
        },
    },
    {
        name: '기업15',
        visualizationData: {
            안정성: 3,
            성장성: 2,
            활동성: 3,
            수익성: 1,
            가용성: 2,
        },
    },
    {
        name: '기업16',
        visualizationData: {
            안정성: 4,
            성장성: 1,
            활동성: 3,
            수익성: -1,
            가용성: -2,
        },
    },
];
const matches = ref([]);
const winners = ref([]);
const winner = ref('');
const currentMatchIndex = ref(0);
let currentRound = ref('16강');
const selectionConfirmed = ref(false);

const chartCanvas1 = ref(null);
const chartCanvas2 = ref(null);
// 차트 인스턴스를 저장할 변수 선언

let chartInstance1 = null;
let chartInstance2 = null;

// 대진 생성 함수 수정
const createMatches = () => {
    const newMatches = [];
    for (let i = 0; i < companies.length; i += 2) {
        newMatches.push([companies[i], companies[i + 1]]);
        console.log(newMatches);
    }
    matches.value = newMatches;
    // 첫 번째 대진에 대해 차트 그리기
    if (newMatches.length > 0) {
        console.log(newMatches[0][0]);
        console.log(newMatches[0][1]);
        console.log(newMatches[0][0].visualizationData);
        console.log(newMatches[0][1].visualizationData);
        chartInstance1 = renderChart(
            chartCanvas1,
            newMatches[0][0].visualizationData,
            chartInstance1
        );
        chartInstance2 = renderChart(
            chartCanvas2,
            newMatches[0][1].visualizationData,
            chartInstance2
        );
    }
};

// 차트 렌더링 함수 수정
const renderChart = (canvasRef, data, chartInstance) => {
    if (canvasRef.value !== null) {
        const ctx = canvasRef.value.getContext('2d');
        // 새 차트 인스턴스 생성 및 할당
        return new Chart(ctx, {
            type: 'radar',
            data: {
                labels: Object.keys(data),
                datasets: [
                    {
                        label: '점수',
                        data: Object.values(data),
                        backgroundColor: 'rgba(0, 123, 255, 0.7)',
                        borderColor: 'rgba(0, 123, 255, 1)',
                        borderWidth: 1,
                    },
                ],
            },
            options: {
                scales: {
                    r: {
                        max: 9,
                        min: -9,
                        stepSize: 3,
                    },
                },
                plugins: {
                    legend: {
                        position: 'top',
                    },
                    datalabels: {
                        display: true,
                        color: '#000',
                        font: {
                            weight: 'bold',
                        },
                    },
                },
            },
        });
    }
};

const isTournamentStarted = ref(false);
// 대진 시작 함수
const startTournament = () => {
    isTournamentStarted.value = true; // 대진 시작 시 true로 설정
    if (!matches.value.length || currentRound.value === '16강') {
        matches.value = [];
        winners.value = [];
        winner.value = '';
        currentMatchIndex.value = 0;
        currentRound.value = '16강';
        createMatches(); // 대진 생성
    }
};

// 대회를 재설정하는 함수
const resetTournament = () => {
    isTournamentStarted.value = false; // 대회 재설정 시 false로 설정하여 버튼을 다시 표시
    matches.value = []; // 대진 배열을 비웁니다.
    winners.value = []; // 우승자 배열을 비웁니다.
    winner.value = ''; // 최종 우승자를 비웁니다.
    currentMatchIndex.value = 0; // 현재 대진 인덱스를 초기화합니다.
    currentRound.value = '16강'; // 현재 라운드를 '16강'으로 초기화합니다.
    createMatches(); // 대진 생성
};

// 기업 선택하는 함수
const selectWinner = (matchIndex, winnerIndex) => {
    winners.value.push(matches.value[matchIndex][winnerIndex]);

    // 선택 확인 딜레이 시작
    selectionConfirmed.value = false;
    setTimeout(() => {
        selectionConfirmed.value = true;
        // 다음 대진으로 이동
        currentMatchIndex.value++;

        // 모든 대진이 끝나면 다음 라운드로
        if (currentMatchIndex.value >= matches.value.length) {
            startNextRound();
        } else {
            // 다음 대진의 시각화 그래프 생성
            const nextMatch = matches.value[currentMatchIndex.value];

            // 이전 차트 인스턴스가 존재하는지 확인하고 파괴합니다.
            if (chartInstance1) {
                chartInstance1.destroy();
            }
            if (chartInstance2) {
                chartInstance2.destroy();
            }

            // 새로운 차트 인스턴스 생성
            chartInstance1 = renderChart(
                chartCanvas1,
                nextMatch[0].visualizationData,
                chartInstance1
            );
            chartInstance2 = renderChart(
                chartCanvas2,
                nextMatch[1].visualizationData,
                chartInstance2
            );
        }
    }, 50); // 1초 딜레이
};
// 다음 대진의 시각화 그래프를 그리는 함수
const renderNextMatchCharts = () => {
    const nextMatch = matches.value[currentMatchIndex.value];
    const company1Data = nextMatch[0].visualizationData;
    const company2Data = nextMatch[1].visualizationData;

    // 차트를 그리기 전에 기존 차트 인스턴스를 파괴합니다.
    if (chartInstance1) {
        chartInstance1.destroy();
    }
    if (chartInstance2) {
        chartInstance2.destroy();
    }

    // 차트를 그립니다.
    chartInstance1 = renderChart(chartCanvas1, company1Data, null);
    chartInstance2 = renderChart(chartCanvas2, company2Data, null);
};
// 다음 라운드를 시작하는 함수
const startNextRound = () => {
    let nextRoundName; // 다음 라운드 이름을 임시 저장할 변수

    // 다음 라운드 이름 결정
    if (winners.value.length === 8) {
        nextRoundName = '8강';
    } else if (winners.value.length === 4) {
        nextRoundName = '4강';
    } else if (winners.value.length === 2) {
        nextRoundName = '결승';
    } else if (winners.value.length === 1) {
        nextRoundName = '우승';
    }

    // 2초 동안 nextRoundName을 currentRound에 할당하여 표시
    currentRound.value = nextRoundName;

    setTimeout(() => {
        // 2초 지연 후 로직 실행
        if (winners.value.length === 1) {
            winner.value = winners.value[0]; // 우승자 결정
        } else {
            matches.value = winners.value
                .map((val, idx, arr) => (idx % 2 === 0 ? [val, arr[idx + 1]] : null))
                .filter(Boolean); // 다음 라운드 대진 생성
            winners.value = []; // 우승자 배열 초기화
            currentMatchIndex.value = 0; // 대진 인덱스 초기화

            // 실제 라운드명 업데이트
            currentRound.value = nextRoundName;
        }

        // 애니메이션 재설정
        isNextRound.value = true;
        setTimeout(() => (isNextRound.value = false), 500);
    }, 1000); // 2초 지연
};

// 컴포넌트가 생성될 때 차트를 초기화하고 렌더링합니다.
onMounted(() => {
    if (matches.value.length > 0) {
        chartInstance1 = renderChart(
            chartCanvas1,
            matches.value[0][0].visualizationData,
            chartInstance1
        );
        chartInstance2 = renderChart(
            chartCanvas2,
            matches.value[0][1].visualizationData,
            chartInstance2
        );
    }
});

// 컴포넌트가 제거될 때 이전 차트를 파기합니다.
onUnmounted(() => {
    if (chartInstance1) {
        chartInstance1.destroy();
    }
    if (chartInstance2) {
        chartInstance2.destroy();
    }
});
</script>

<style scoped>
@import url('@/assets/choice.css');
/* 페이드 애니메이션 정의 */
/* 페이드 아웃에서 인으로 애니메이션 정의 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.2s ease;
}
/* startNextRound 호출 시 적용될 애니메이션 스타일 */
.fade-next-round-enter-active,
.fade-next-round-leave-active {
    transition: opacity 0.2s ease-out;
}
</style>

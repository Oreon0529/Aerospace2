/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Firebase Admin 초기화
admin.initializeApp();

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

// 6개월 지난 사물함 데이터를 초기화하는 함수
exports.clearExpiredLockers = functions.pubsub.schedule("every 24 hours").onRun(async () => {
    const db = admin.database();
    const paths = ["/Lockers", "/Lockers5"]; // 2층과 5층 경로
    const now = new Date(); // 현재 날짜

    for (const path of paths) {
        const lockersRef = db.ref(path); // 각 경로에 접근
        const snapshot = await lockersRef.once("value"); // 경로의 모든 데이터 가져오기
        const updates = {}; // 초기화할 데이터 저장

        snapshot.forEach((child) => {
            const locker = child.val();
            const useUntil = locker.useUntil || ""; // `useUntil` 값 가져오기

            if (useUntil) {
                const useUntilDate = new Date(useUntil); // `useUntil`을 Date 객체로 변환
                if (useUntilDate < new Date()) {
                    // 현재 날짜보다 이전이면 초기화
                    updates[child.key] = {
                        isUsed: false,
                        useUntil: "",
                        studentId: ""
                    };
                }
            }
        });

        if (Object.keys(updates).length > 0) {
            await lockersRef.update(updates); // 초기화된 데이터 Firebase에 업데이트
            logger.info(`초기화된 데이터 경로: ${path}`);
        } else {
            logger.info(`만료된 데이터가 없습니다. 경로: ${path}`);
        }
    }

    logger.info("모든 사물함 초기화 작업 완료");
    return null;
});

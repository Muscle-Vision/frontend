package com.example.musclevision.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musclevision.R

@Composable
fun FirstNeckStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.neck_1),
                contentDescription = "첫번째 목스트레칭",
                modifier = Modifier.size(120.dp)
            )
            
            Text(
                text = "앞 목 스트레칭",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("1. 양 손으로 쇄골 아래를 누른 상태에서, 숨을 들이마셨다가 " +
                "\"후~\" 내쉬면서 고개를 천천히 뒤로 젖힌다. \n이 때 몸이 함께 젖혀지지 않도록 하고, 쇄골 위부터 목까지 근육만 쭉 늘어나 도록 해야 한다.\n\n" +
                "2. 고개를 최대한 젖히고, 10초 유지한다.\n\n" +
                "3. 5회 반복한다.\n"
        , color = Color.Black)
    }
}
@Composable
fun SecondNeckStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.neck_2),
                contentDescription = "두번째 목스트레칭",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "목빗근 스트레칭",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("1. 목빗근을 검지와 중지를 이용하여 뒤통수가 시작되는 부분에서 쇄골까지 천천히 꾹꾹 눌러줍니다. (양쪽 3번 반복)\n\n"+
                "2. 쇄골 윗쪽 목빗근이 시작되는 부분을 누른 상태로 반대편 대각선으로 목을 쭉~ 30초 정도 늘려줍니다.\n"+
                " * 시선도 목을 늘려준 방향을 향하는 것이 좋아요.\n\n" +
                "3. 반대편도 반복합니다. 3세트 반복!"
            , color = Color.Black)
    }
}
@Composable
fun FirstShoulderStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.shoulder_1),
                contentDescription = "첫번째 어깨스트레칭",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "어깨 교정운동",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("1. 어깨가 올라간 쪽의 반대편(왼쪽 어깨가 높으면 오른쪽) 손바닥으로 뒷머리에 대고 고개를 오른쪽으로 살짝 돌린다.\n\n" +
                "2. 오른손으로 뒷머리를 당기면서 왼쪽 어깨를 아래로 쭉 끌어내리며 어깨를 자극한다. 20~30초간 상태 유지. \n이때, 머리를 과하게 당기지 않고, 허리가 휘지 않도록 주의한다.\n"
            , color = Color.Black)
    }
}
@Composable
fun SecondShoulderStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.shoulder_2),
                contentDescription = "두번째 어깨스트레칭",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "어깨 교정운동",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("1. 바닥에 엎드린 채 어깨가 내려간 쪽(왼쪽)의 팔을 뻗고, 반대편(오른쪽) 팔꿈치를 접어 머리를 맞대고 엎드린다.\n\n" +
                "2. 상태 그대로 왼팔을 귀 옆까지 올리며 어깨를 자극한다. 동작을 10~20회 반복한다\n"
            , color = Color.Black)
    }
}
@Composable
fun ThirdShoulderStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.shoulder_3),
                contentDescription = "세번째 어깨운동",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "어깨 교정운동",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("1. 어깨가 내려간 쪽(오른쪽)의 반대편(왼쪽) 손바닥으로 오른쪽 어깨를 잡는다.\n\n" +
                "2. 상태 그대로 오른쪽 팔을 아래에서 위로 올리며 어깨를 자극한다. 동작을 10~20회 반복한다.\n"
            , color = Color.Black)
    }
}
@Composable
fun FirstPelvisStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.pelvis_1),
                contentDescription = "첫번째 골반스트레칭",
                modifier = Modifier.size(120.dp))

            Text(
                text = "피라미드 스트레칭",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("1. 양손과 양발을 바닥에 대고 엎드립니다. 한 발이 앞으로 더 나와도 무관합니다.\n\n" +
                "2. 팔꿈치와 무릎을 약간 구부리면서 상체를 들어올립니다.\n\n" +
                "3. 상체를 들어올리면서 허리 부분을 길게 펴고 엉덩이를 후퇴시킵니다.\n\n" +
                "4. 이 상태로 10~15초 동안 유지한 후, 천천히 원래 자세로 돌아옵니다.\n\n" +
                "5. 이를 3~5세트 반복합니다", color = Color.Black)
    }
}
@Composable
fun SecondPelvisStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.pelvis_2),
                contentDescription = "두번째 골반 스트레칭",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "다리 들기 운동",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("1. 바닥에 등을 대고 누워서 다리를 일직선으로 펴줍니다.\n\n" +
                "2. 한 쪽 다리를 천천히 들어올려 골반을 교정합니다.\n\n" +
                "3. 들어올린 다리를 5~10초 동안 유지한 후, 천천히 내려놓습니다.\n\n" +
                "4. 반대쪽 다리도 같은 방법으로 반복합니다.\n\n" +
                "5. 이를 3~5세트 반복합니다.\n"
            , color = Color.Black)
    }
}
@Composable
fun ThirdPelvisStretching(){
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painter = painterResource(id = R.drawable.pelvis_3),
                contentDescription = "세번째 골반스트레칭",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "플랭크",
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text("1. 바닥에 팔꿈치와 발끝을 대고 엎드립니다.\n\n" +
                "2. 팔꿈치는 어깨 아래에 위치하고, 몸은 일직선을 이루도록 합니다.\n\n" +
                "3. 복부와 엉덩이 근육을 긴장시켜 유지합니다.\n\n" +
                "4. 이 상태로 30초에서 1분간 유지합니다.\n\n" +
                "5. 천천히 휴식을 취한 후, 3~5세트를 반복합니다.\n"
            , color = Color.Black)
    }
}

package com.example.trivia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.trivia.data.MyViewModel
import com.example.trivia.data.ViewModelFactory
import com.example.trivia.database.QuestionEntity
import com.example.trivia.game.Processor
import com.example.trivia.ui.theme.TriviaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ViewModelFactory (this.application)
        val viewModel = factory.let { ViewModelProvider (this, it)[MyViewModel :: class.java] }
        val processor=Processor()
        viewModel.process()
        setContent {
            TriviaTheme {

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MyScreen(vm = viewModel, processor = processor)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String,modifier:Modifier=Modifier) {
    Column(modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top) {
        Text(text = "Welcome to Trivia App", fontWeight = FontWeight.Bold)
        Image(painter = painterResource(id = R.drawable.clover_svgrepo_com), contentDescription = null, modifier = modifier
            .padding(20.dp)
            .size(100.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxHeight(1f)) {
            Button(onClick = { /*TODO*/ }, modifier = modifier
                .weight(1f)
                .padding(10.dp))
            {
                Text("Play offline")
            }
            Button(onClick = { /*TODO*/ },modifier = modifier
                .weight(1f)
                .padding(10.dp)) {
                Text(text ="Play online")

        }
        }

    }

}
@Composable
fun MyScreen(modifier:Modifier=Modifier,vm:MyViewModel,processor: Processor) {
    val focusManager = LocalFocusManager.current
    val text = remember{ mutableStateOf("") }
    var progress by remember { mutableStateOf(0.0f) }
    var textFieldValue by remember { mutableStateOf("") }
    var pair=vm.quote
    processor.question=pair.question
    processor.answer=pair.answer
    fun update()
    {
        vm.process()
        pair=vm.quote
        processor.answer=pair.answer
        processor.question=pair.question
        textFieldValue=" "
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            , horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = processor.question,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
        )


        Text(
            text = if (text.value=="") processor.get_Cipher() else text.value,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
        )
        Text(
            text = "Текущий счет ${processor.score}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = { Text("“Введите ответ”") },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)

            )
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(progress)
        LaunchedEffect(Unit) {
            while (true) {
                delay(1000L)
                progress += 0.01f
                if (progress >= 1.0f) {
                    update()
                    progress = 0.0f
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {text.value=processor.getHint().first}) {
            Text(text = "Взять подсказку")
        }
        Button(onClick = {text.value=processor.get_mixed()}) {
            Text(text = "Перемешать ответ")
        }
        Button(onClick = {update();progress=0.0f;processor.erase_counter()}) {
            Text(text = "Следующий вопрос")
        }
        Button(onClick = {
            processor.tryAnswer = textFieldValue
            if (processor.isAnswerRight()) {
                update()

            }
        }) {
            Text(text = "Ответить")
        }
    }
//    var users by remember { mutableStateOf(emptyList<QuestionEntity>()) }
//    LaunchedEffect(true) {
//        users = withContext(Dispatchers.IO) {
//            vm.quotes.first() // coroutine function to retrieve rows
//        }
//    }
//    LazyColumn {
//        items(users.size) {
//            Text(text = it.toString())
//        }
//    }
    }
/*TODO(LazyColumn and coroutines viewmodel fetch data from DAO)*/

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TriviaTheme {
        Greeting("Android", modifier = Modifier)
    }
}


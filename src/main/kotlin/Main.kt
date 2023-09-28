import Res.Colors
import Res.Resource
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sannimith.khmerdictionary.Database
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dictionary.Lexicalentry

@Composable
@Preview
fun App() {
    val driver : SqlDriver = JdbcSqliteDriver("jdbc:sqlite:chounnath.db")
    val database = Database(driver = driver)
    val list = mutableStateListOf<Lexicalentry>()
    var findWord by remember { mutableStateOf("") }
    var wordId by remember { mutableStateOf("1") }

    val state = rememberLazyListState()

    LaunchedEffect(Unit) {
        list.clear()
        list.addAll(database.chounNathQueries.selectAllWords().executeAsList())
    }
    MaterialTheme {
        Row {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .border(
                        border = BorderStroke(width = 1.dp,
                            color = Color.Black),
                        shape = RoundedCornerShape(8.dp))
                    .width(280.dp)
                    .fillMaxHeight()
                    .background(color = Colors.lightPaper, shape = RoundedCornerShape(8.dp))
            ) {
                Column {
                    Row {
                        OutlinedTextField(
                            value = findWord,
                            onValueChange = {newText ->
                                findWord = newText
                                list.clear()
                                list.addAll(database.chounNathQueries.findWord(searchWord = findWord).executeAsList())
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(5.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Colors.darkPaper
                            ),
                            textStyle = TextStyle(
                                fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT)),
                                fontSize = 18.sp
                            )
                        )
                    }
                    var selectedIndex by remember {
                        mutableStateOf(0)
                    }
                    Box {
                        LazyColumn(state = state) {
                            itemsIndexed(list) { index,it ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .selectableGroup()
                                        .clickable {
                                            selectedIndex = index
                                            wordId = it.id.toString()
                                        }
                                        .background(color = if (index == selectedIndex) Colors.darkPaper else Color.Transparent)
                                ){
                                    Row(modifier = Modifier.padding(5.dp)){
                                        Text(
                                            it.writtenForm.toString(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                                        )
                                        Spacer(Modifier.padding(5.dp))
                                        if(it.partOfSpeech.toString().isNotEmpty()) {
                                            Text(
                                                text = "(${it.partOfSpeech.toString()})",
                                                fontSize = 20.sp,
                                                color = Color.Red,
                                                fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(
                                scrollState = state
                            )
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(border = BorderStroke(width = 1.dp, color = Color.Black), shape = RoundedCornerShape(8.dp))
                    .background(color = Colors.darkPaper, shape = RoundedCornerShape(8.dp))
            ) {
                val diList = database.chounNathQueries.selectDifinition(id = wordId.toLong()).executeAsList()
                val exList = database.chounNathQueries.selectExample(id = wordId.toLong()).executeAsList()
                val word = database.chounNathQueries.selectWord(id = wordId.toLong()).executeAsOne().writtenForm.toString()
                val pronunciation = database.chounNathQueries.selectPronunciation(id = wordId.toLong()).executeAsOne().pronunciation.toString()
                val partOfSpeech = database.chounNathQueries.selectPartOfSpeech(id = wordId.toLong()).executeAsOne().partOfSpeech.toString()
                SelectionContainer {
                    Column(modifier = Modifier.padding(5.dp)) {
                        Text(
                            text = word,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                        )
                        if (pronunciation.isNotEmpty()) {
                            Text(
                                text = pronunciation,
                                fontSize = 15.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                            )
                        }
                        if (partOfSpeech.isNotEmpty()) {
                            Text(
                                text = "($partOfSpeech)",
                                fontSize = 15.sp,
                                color = Color.Red,
                                fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                            )
                        }
                        for ((di, ex) in diList.zip(exList)) {
                            Text(
                                text = di.definition.toString(),
                                fontSize = 15.sp,
                                fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                            )
                            if (ex.example.toString().isNotEmpty()) {
                                Column {
                                    Text(
                                        text = "ឧទាហរណ៍៖ ",
                                        fontSize = 15.sp,
                                        color = Color.Magenta,
                                        fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                                    )
                                    Text(
                                        text = ex.example.toString(),
                                        fontSize = 15.sp,
                                        color = Color.Blue,
                                        fontFamily = FontFamily(Font(Resource.SIEMREAB_FONT))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

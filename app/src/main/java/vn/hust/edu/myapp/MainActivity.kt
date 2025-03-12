package vn.hust.edu.myapp
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import vn.hust.edu.myapp.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var canAddOperator = false
    private var canAddDecimal = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.workingview.text = ""
        binding.resultview.text = ""
        //setOnclickListener
        binding.CE.setOnClickListener(this)
        binding.C.setOnClickListener(this)
        binding.BS.setOnClickListener(this)
        binding.divide.setOnClickListener(this)
        binding.seven.setOnClickListener(this)
        binding.eight.setOnClickListener(this)
        binding.nine.setOnClickListener(this)
        binding.multiply.setOnClickListener(this)
        binding.four.setOnClickListener(this)
        binding.five.setOnClickListener(this)
        binding.six.setOnClickListener(this)
        binding.minus.setOnClickListener(this)
        binding.one.setOnClickListener(this)
        binding.two.setOnClickListener(this)
        binding.three.setOnClickListener(this)
        binding.plus.setOnClickListener(this)
        binding.sign.setOnClickListener(this)
        binding.zero.setOnClickListener(this)
        binding.comma.setOnClickListener(this)
        binding.equal.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.CE.id -> clearEntry()
            binding.C.id -> clearAll()
            binding.BS.id -> backspace()
            binding.divide.id -> appendOperator("/")
            binding.multiply.id -> appendOperator("*")
            binding.minus.id -> appendOperator("-")
            binding.plus.id -> appendOperator("+")
            binding.equal.id -> equalsAction()
            binding.sign.id -> changeSign()
            binding.comma.id -> addDecimal()
            binding.seven.id -> appendNumber("7")
            binding.eight.id -> appendNumber("8")
            binding.nine.id -> appendNumber("9")
            binding.four.id -> appendNumber("4")
            binding.five.id -> appendNumber("5")
            binding.six.id -> appendNumber("6")
            binding.one.id -> appendNumber("1")
            binding.two.id -> appendNumber("2")
            binding.three.id -> appendNumber("3")
            binding.zero.id -> appendNumber("0")
        }
    }
    //append number
    private fun appendNumber(number: String) {
        binding.workingview.append(number)
        canAddOperator=true
    }
    //
    private fun addDecimal() {
        if (canAddDecimal) {
            binding.workingview.append(".")
            canAddDecimal = false
        }
    }
    //append operator
    private fun appendOperator(operator: String) {
        if (canAddOperator) {
            binding.workingview.append(operator)
            canAddOperator = false
            canAddDecimal = true
        }
    }
        //calculate
        private fun calculate():String {
            val digitsOperators = digitsOperators()
            if(digitsOperators.isEmpty()) return ""
            val timesDivision=timesDivisionCalculate(digitsOperators)
            if(timesDivision.isEmpty()) return ""
            val result=addSubtractCalculate(timesDivision)
            return result.toString()
        }

        private fun equalsAction() {
            try {
                val result=calculate()
                if(result.isEmpty()) throw IllegalStateException("Invalid expression")
                binding.resultview.text = result
            }catch (e:Exception){
                binding.resultview.text="Error:${e.message}"
            }
        }

        //clear entry
        private fun clearEntry() {
            val text=binding.workingview.text.toString()
            if(text.isEmpty()||text=="") return
            val lastOperatorIndex=text.dropLast(1).lastIndexOfAny(charArrayOf('+','-','*','/'))
            binding.workingview.text=if (lastOperatorIndex!=-1) {
                text.substring(0, lastOperatorIndex + 1)
            }
                else{
                    ""
                }
            }

        //clear all
        private fun clearAll() {
            binding.workingview.text = ""
            binding.resultview.text = ""
        }

        //backspace
        private fun backspace() {
            val length = binding.workingview.length()
            if (length > 0)
                binding.workingview.text = binding.workingview.text.subSequence(0, length - 1)
            else {
                binding.workingview.text=""
            }
        }

        //change sign
        private fun changeSign() {
            val currentText=binding.workingview.text.toString()
            if(currentText.isNotEmpty()&&currentText!="0") {
                binding.workingview.text = if (currentText.startsWith("-")) {
                    currentText.substring(1)
                }
                else{
                    "-$currentText"
                }
            }
            }

        private fun timesDivisionCalculate(passList: MutableList<Any>):MutableList<Any>
        {
            var list=passList
            while(list.contains('*')||list.contains('/'))
            {
                list=calcTimesDiv(list)
            }
            return list
        }
        private fun calcTimesDiv(passList: MutableList<Any>):MutableList<Any> {
            val newList = mutableListOf<Any>()
            var restartIndex = passList.size
            try {
                for (i in passList.indices) {
                    if (passList[i] is Char && i != passList.lastIndex && i < restartIndex) {
                        val operator = passList[i]
                        val prevDigit = passList[i - 1] as Float
                        val nextDigit = passList[i + 1] as Float
                        when (operator) {
                            '*' -> {
                                newList.add(prevDigit * nextDigit)
                                restartIndex = i + 1
                            }

                            '/' -> {
                                if (nextDigit == 0f) throw ArithmeticException("Invalid expression")
                                newList.add(prevDigit / nextDigit)
                                restartIndex = i + 1
                            }
                            else -> {
                                newList.add(prevDigit)
                                newList.add(operator)
                            }
                        }
                    }
                    if (i > restartIndex) {
                        newList.add((passList[i]))
                    }
                }
                } catch (e:Exception){
                    binding.resultview.text="Error:${e.message}"
                return mutableListOf()
            }
            return newList
        }
        private fun addSubtractCalculate(passList:MutableList<Any>):Float
        {
            var result=passList[0] as Float
            for(i in passList.indices)
            {
                if(passList[i] is Char&& i!=passList.lastIndex)
                {
                    val operator=passList[i]
                    val nextDigit=passList[i+1] as Float
                    if(operator=='+')
                        result+=nextDigit
                    if(operator=='-')
                        result-=nextDigit
                }
            }
            return result
        }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        var lastCharWasOperator = true
        for (character in binding.workingview.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
                lastCharWasOperator = false
            } else {
                if (character == '-' && lastCharWasOperator) {
                    currentDigit += character
                } else {
                    if (currentDigit.isNotEmpty()) {
                        list.add(currentDigit.toFloat())
                    }
                    currentDigit = ""
                    list.add(character)
                    lastCharWasOperator = true
                }
            }
        }
        if (currentDigit.isNotEmpty())
            list.add(currentDigit.toFloat())

        return list
    }

}



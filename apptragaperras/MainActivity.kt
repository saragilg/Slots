package net.azarquiel.apptragaperras

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.appcompat.v7.Appcompat
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {
    var animaciones = intArrayOf(R.drawable.animacion0, R.drawable.animacion1, R.drawable.animacion2)
    val nombImagenes = arrayOf("fresa", "cereza", "limon", "dolar", "campana", "siete")
    var ivs = arrayOfNulls<ImageView>(3)
    var frames = arrayOfNulls<AnimationDrawable>(3)
    var ivsJugada = IntArray(3)
    var credito=50
    lateinit var mp: MediaPlayer
    var pulsado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nuevaPartida()
    }

    private fun nuevaPartida(){
        Toast.makeText(this, "Nueva partida, ¡suerte!", Toast.LENGTH_SHORT).show()
        credito=50
        initFrame()
        btnStart.setOnClickListener { btnStartOnClick() }
    }

    private fun initFrame() {
        mp = MediaPlayer.create(this, R.raw.maquina)
        mp.isLooping = true

        txtPuntos.text = "$credito"

        var iv:ImageView
        //cuando se inicia ponemos la animacion en las imagenes
        for (i in 0 until linearIvs.childCount){
            iv = linearIvs.getChildAt(i) as ImageView
            //guardamos las imagenes en array
            ivs[i] = iv
            var n = (Math.random()*6).toInt()
            var id = resources.getIdentifier(nombImagenes[i], "drawable", packageName)
            //tapa inicio al azar
            iv.setImageResource(id)
            //animaciones
            iv.setBackgroundResource(animaciones[i])
            //get de las animaciones
            frames[i] = iv.background as AnimationDrawable
        }
    }

    private fun btnStartOnClick() {
        if (pulsado) return
        pulsado = true
        mp.start()
        //quitamos la tapa
        for (i in 0 until linearIvs.childCount){
            ivs[i]?.setImageResource(android.R.color.transparent)
            frames[i]?.start()
            //hilo
            parar(i)
        }
    }

    private fun parar(i: Int) {
        doAsync {
            var result = duerme(1000*(i+1),i)
            uiThread {
                tapa(result)
                if (i==2){
                    mp.pause()
                    puntos()
                    pulsado = false
                }
            }
        }
    }

    private fun duerme(time:Int, i:Int): Int {
        SystemClock.sleep(time.toLong())
        return i
    }

    private fun tapa(i: Int) {
        //paramos y pintamos una al azar
        frames[i]!!.stop()
        imgTapa(i)
    }

    private fun imgTapa(i: Int) {
        val n = (Math.random()*6).toInt()
        val id = resources.getIdentifier(nombImagenes[n],"drawable",packageName)
        ivs[i]!!.setImageResource(id)

        //guardamos las tapas
        ivsJugada[i] = n

    }

    private fun puntos(){
        var iguales = true
        //comparamos las imagenes que han salido
        for (i in 0 until ivsJugada.size - 1){
            if (ivsJugada[i] != ivsJugada[i + 1]){
                iguales=false
            }
        }
        //miramos si las iguales son 7 o son otra imagen
        if (iguales){
            if (ivsJugada[0]==5){
                Toast.makeText(this, "ENHORABUENA!! HAS GANADO LA PARTIDA.", Toast.LENGTH_LONG).show()
                nuevaPartida()
            }else{
                credito+=10
                txtPuntos.text="$credito"
                Toast.makeText(this, "¡Sacaste 3 iguales!", Toast.LENGTH_LONG).show()
            }
        }else{
            credito-=1
            txtPuntos.text="$credito"
        }

        //si se acaba el credito empieza una nueva partida
        if (credito == 0){
            nuevaPartida()
        }
    }
}

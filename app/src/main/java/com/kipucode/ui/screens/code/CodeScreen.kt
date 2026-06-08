package com.kipucode.ui.screens.code

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kipucode.ui.theme.BackgroundGray
import dev.jeziellago.compose.markdowntext.MarkdownText


@Composable
fun CodeScreen(
    contentLesson : String?
){
    LazyColumn(
        Modifier.fillMaxSize().background(BackgroundGray),
    ) {
        item {


        }

        item {
            if(contentLesson != null){
                ContentMarkdown(content = contentLesson)
            } else {
                ContentMarkdown(content = """
                    ## Selecciona una lección para comenzar a aprender a programar.
                    
                    Vuelve a la pantalla de inicio y elige una lección para sumergirte en el mundo de la programación. 
                    Cada lección está diseñada para guiarte paso a paso, desde los conceptos básicos hasta temas más avanzados.
                    ¡Tu aventura de aprendizaje comienza aquí!
                """.trimIndent())
                }
        }
    }
}

@Composable
fun ContentMarkdown(content: String){

    val markdown = content.trimIndent()
    MarkdownText(
        modifier = Modifier.padding(16.dp),
        markdown = markdown
    )
}

@Preview
@Composable
fun PreviewMarkdown(){
    CodeScreen("""
        # Lección: Pensamiento Computacional y Lenguajes Formales

        ¡Bienvenido! Antes de escribir tu primera línea de código en Python, necesitas aprender a "pensar" como un programador. No te preocupes, no es magia ni matemáticas avanzadas; es simplemente una forma de resolver problemas paso a paso.

        Esta lección está diseñada en pequeñas cápsulas para que tu cerebro asimile los conceptos de forma natural y sin abrumarse.

        ---

        ## 🧠 1. ¿Qué es el Pensamiento Computacional?

        Imagina que tienes que organizar una fiesta sorpresa gigante. Si intentas pensar en todo a la vez, te vas a estresar. El **Pensamiento Computacional** es la habilidad de tomar un problema grande y complejo, y desarmarlo en partes tan pequeñas y lógicas que hasta una computadora (que no tiene sentido común) pueda entenderlo y resolverlo.

        Para lograr esto, utilizamos **cuatro pilares fundamentales**:

        ### Los 4 Pilares del Pensamiento Computacional

        * **🔪 1. Descomposición:** Romper el problema grande en partes pequeñas.
            * *Ejemplo de la fiesta:* En lugar de "hacer la fiesta", lo divides en: buscar lugar, comprar comida, invitar gente y elegir música.
        * **🔍 2. Reconocimiento de Patrones:** Buscar similitudes o cosas que se repiten.
            * *Ejemplo de la fiesta:* Notar que a todos tus amigos les gusta la pizza, así que el patrón te dice que comprar pizza es una apuesta segura.
        * **🗑️ 3. Abstracción:** Ignorar los detalles innecesarios y enfocarse en lo importante.
            * *Ejemplo de la fiesta:* No importa de qué color son los vasos, solo importa que haya suficientes para todos.
        * **📝 4. Algoritmos:** Crear instrucciones paso a paso para resolver el problema.
            * *Ejemplo de la fiesta:* 1. Alquilar local. 2. Enviar invitaciones. 3. Comprar pizzas. 4. Poner música a las 8:00 PM.

        ---

        ## 🗣️ 2. Lenguajes Naturales vs. Lenguajes Formales

        Ahora que sabes *cómo* estructurar la solución en tu cabeza, necesitas comunicársela a la computadora. Aquí es donde entran los lenguajes.

        ### Lenguajes Naturales (Como el español o el inglés)
        Son los que usamos para hablar entre humanos. Son hermosos, pero tienen un gran problema para las computadoras: **son ambiguos**.

        > *Si te digo: "Pásame el gato"*, ¿me estás pasando a tu mascota felina o la herramienta para levantar el auto? Un humano lo entiende por el contexto; una computadora se confunde y falla.

        ### Lenguajes Formales (Como Python o las Matemáticas)
        Fueron creados por humanos para tareas específicas. Tienen reglas estrictas de vocabulario y gramática (sintaxis). **Cero ambigüedad.**

        > *En un lenguaje formal, 2 + 2 siempre es 4.* No hay doble sentido, ni sarcasmo, ni interpretaciones. Si escribes una instrucción, la computadora hará exactamente eso. Ni más, ni menos.

        ---

        ## 🐍 3. ¿Dónde encaja Python en todo esto?

        En este curso, **Python** será tu Lenguaje Formal. 

        1. Tú usas el **Pensamiento Computacional** para idear la solución (el algoritmo).
        2. Luego, traduces ese algoritmo a **Python** (el lenguaje formal).
        3. La computadora lee las reglas estrictas de Python y ejecuta la tarea a la perfección.

        Python es ideal para empezar porque su diseño es muy amigable; se lee casi como si fuera inglés, lo que te permite concentrarte más en la lógica (tu pensamiento) y menos en memorizar símbolos extraños.

        ---

        ## 🎯 4. Reto Cognitivo: Ponlo en práctica

        Para consolidar lo que acabas de aprender, haz este pequeño ejercicio mental:

        **El Problema:** Tienes que preparar un sándwich de mermelada y mantequilla de maní. 
        Intenta aplicar un solo pilar: el **Algoritmo**. 

        Si le dieras las instrucciones a un robot que literalmente no sabe nada del mundo, ¿qué pasaría si le dices *"Pon la mermelada en el pan"*? ¡El robot aplastaría el frasco de vidrio entero sobre la bolsa de pan cerrado! 

        * **Paso 1:** Abre la bolsa de pan.
        * **Paso 2:** Saca dos rebanadas.
        * **Paso 3:** Abre el frasco de mantequilla de maní...

        Esa precisión extrema es el corazón del pensamiento computacional y la base de todo lo que programarás de ahora en adelante.
    """.trimIndent())
}
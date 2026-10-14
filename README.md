# Cannon Game App 

**Disciplina:** Programação III - Sistemas de Informação  
**Instituição:** UEMG (Unidade Passos)  
**Integrantes:** Mateus Carvalho Rodrigues da Silva e Anna Luiza Silva Dome

##  Objetivo do Projeto
Este projeto consiste no desenvolvimento colaborativo da aplicação "Cannon Game", baseada no Capítulo 6 do livro *Android 6 for Programmers*. O objetivo é aplicar conceitos práticos de programação mobile em Android, focando em gráficos de baixo nível, interatividade e desempenho.

##  Como Executar a Aplicação
1. Clone este repositório: `git clone https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git`
2. Abra o projeto no **Android Studio**.
3. Aguarde o Gradle sincronizar todas as dependências.
4. Execute o app em um emulador ou dispositivo físico rodando Android (preferencialmente em modo paisagem).

## ️ Funcionalidades Implementadas
- **Animação Frame a Frame:** Utilização de `Thread` exclusiva (`CannonThread`) para rodar o Game Loop sem travar a UI principal.
- **Manipulação Gráfica:** Uso intensivo de `Canvas` e `Paint` para desenhar o canhão, a bala, a barreira e os alvos diretamente na tela.
- **Controle de Superfície:** Implementação do `SurfaceView` e `SurfaceHolder` para renderização eficiente de gráficos.
- **Eventos de Toque:** Interatividade via `onTouchEvent` para calcular o ângulo do tiro e disparar.
- **Sons e Áudio:** Integração com `SoundPool` para executar sons de disparo (`cannon_fire.wav`), colisão na barreira (`blocker_hit.wav`) e colisão no alvo (`target_hit.wav`).
- **Immersive Mode:** Aplicação roda em tela cheia (Fullscreen), escondendo a barra de status e botões de navegação virtuais.

##  Recursos Extras (Bônus Implementado)
- **Dificuldade Progressiva:** Ao vencer a rodada e clicar em "Novo Jogo", o nível aumenta (variável `currentLevel`). A barreira passa a se mover mais rápido e o tamanho dos alvos diminui, aumentando gradativamente o desafio.
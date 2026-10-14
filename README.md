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

##  Funcionalidades Implementadas
- **Animação Frame a Frame:** Utilização de `Thread` exclusiva (`CannonThread`) para rodar o Game Loop sem travar a UI principal.
- **Manipulação Gráfica:** Uso intensivo de `Canvas` e `Paint` para desenhar o canhão, a bala, a barreira e os alvos diretamente na tela.
- **Controle de Superfície:** Implementação do `SurfaceView` e `SurfaceHolder` para renderização eficiente de gráficos.
- **Eventos de Toque:** Interatividade via `onTouchEvent` para calcular o ângulo do tiro e disparar.
- **Sons e Áudio:** Integração com `SoundPool` para executar sons de disparo (`cannon_fire.wav`), colisão na barreira (`blocker_hit.wav`) e colisão no alvo (`target_hit.wav`).
- **Immersive Mode e Orientação:** Aplicação roda em tela cheia (Fullscreen), escondendo a barra de status e botões virtuais, com orientação travada em modo paisagem (`landscape`) para a melhor experiência física.

##  Recursos Extras (Bônus Implementados)
Além dos requisitos originais do livro, expandimos o projeto com diversas mecânicas e melhorias visuais:
- **Dificuldade Progressiva Avançada:** A cada vitória, o nível do jogo aumenta (variável `currentLevel`). A barreira se move mais rápido, o tamanho dos alvos diminui e, a partir do nível 2, os alvos passam a se mover verticalmente em direções alternadas.
- **Sistema de Partículas (Explosões):** Criação de um mini-motor de partículas customizado. Ao acertar um alvo, cacos coloridos voam pela tela com efeito de física e desaparecem (fade-out). A barreira também emite faíscas ao rebater a bala.
- **Rastro da Bala (Trail):** A bala de canhão agora armazena seu histórico de posições, deixando um rastro visual dinâmico de fumaça que diminui de opacidade e raio ao longo da trajetória.
- **Design com Shaders (Degradês):** Substituição de cores sólidas por `LinearGradient`. O cenário de fundo muda de cor conforme o nível sobe (simulando do entardecer ao espaço noturno). Alvos ganharam aspecto de cristal e a barreira recebeu visual de metal industrial.
- **Trilha Sonora Integrada:** Adição de música de fundo em loop utilizando a classe `MediaPlayer`, com gerenciamento inteligente do ciclo de vida da Activity (pausando e retomando a música ao minimizar/abrir o app).
- **HUD Aprimorado e Responsivo:** As estatísticas (Nível, Tiros e Tempo) foram redesenhadas com fontes em negrito e sombreadas, posicionadas centralmente no topo da tela utilizando alinhamento nativo (`Align.CENTER`) para adaptação perfeita e prevenção de cortes pelo entalhe (*notch*) das câmeras.

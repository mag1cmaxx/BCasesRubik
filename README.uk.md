[Русский](README.md) | [English](README.en.md) | **Українська**

# Rubik

[![Build](https://github.com/mag1cmaxx/BCasesRubik/actions/workflows/build.yml/badge.svg)](https://github.com/mag1cmaxx/BCasesRubik/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/mag1cmaxx/BCasesRubik?label=version&color=blue)](https://github.com/mag1cmaxx/BCasesRubik/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/mag1cmaxx/BCasesRubik/total?color=blue)](https://github.com/mag1cmaxx/BCasesRubik/releases)
![Paper](https://img.shields.io/badge/Paper-1.21.4%2B-2e7d32)
![Java](https://img.shields.io/badge/Java-21-b07219)

Аддон для [BCases](https://github.com/By1337/BCases): над кейсом з'являється об'ємний кубик Рубіка 3×3×3 і сам себе збирає.

1. Деталі злітаються з іскор і складаються в зібраний кубик.
2. Кубик крутиться в повітрі та швидко перемішується, кожен поворот шару видно повністю.
3. Потім він збирається назад. Останні повороти йдуть повільніше.
4. Зібраний кубик спалахує, розлітається на деталі й залишає всередині виграш.

Кубик складається з 81 block display: 27 чорних деталей і 54 наліпки. Усе зроблено на пакетах через VirtualEntityApi, у світ нічого не ставиться. Приз обирається заздалегідь, як і в інших анімаціях BCases.

## Вимоги

- Paper 1.21.4 або новіший
- BCases 1.2
- VirtualEntityApi з підтримкою версії сервера

## Встановлення

1. Завантажити `BCasesRubik-<версія>.jar` з [релізів](https://github.com/mag1cmaxx/BCasesRubik/releases/latest).
2. Покласти його в `plugins/BCases/addons/`.
3. Перезапустити сервер або виконати `/bcases reload`.
4. З'явиться файл `plugins/BCases/animations/rubik.yml`, id анімації `rubik`.

Перевірити: подивитися на блок кейса й ввести `/bcases play rubik <набір призів>`.

## Налаштування

Усі параметри описані в `rubik.yml` (коментарі там російською). Будь-який з них можна видалити, тоді візьметься значення за замовчуванням.

| Параметр | За замовчуванням | Що робить |
|---|---|---|
| `height` | 2.3 | висота центру кубика над низом блока кейса |
| `size` | 0.45 | розмір однієї деталі в блоках, увесь кубик утричі більший |
| `spin` | 3.0 | швидкість обертання всього кубика, градусів за тік |
| `tilt` | 30 | нахил, щоб було видно верхню грань |
| `scramble.moves` | 20 | скільки поворотів під час перемішування |
| `scramble.ticks` | 2 | тіків на поворот під час перемішування |
| `solve.ticks` | 4 | тіків на поворот під час збирання |
| `solve.final_ticks` / `solve.final_moves` | 10 / 3 | уповільнення останніх поворотів |
| `show_ticks` | 80 | скільки тіків показувати приз |
| `colors.*` | бетон | блоки граней і деталей, можна поставити будь-які |

Уся анімація триває приблизно `moves × (scramble.ticks + solve.ticks)` тіків плюс близько 5 секунд на збирання, спалах і показ призу.

## Збирання

```
mvn clean package
```

Потрібна Java 21. Готовий jar буде в `target/`.

#include <stdlib.h>
#include <windows.h>

int main()
{
    SetEnvironmentVariable("LAUNCH_FROM_EXE", "true");
    system("java -jar game.jar");
    system("pause");
    return 0;
}
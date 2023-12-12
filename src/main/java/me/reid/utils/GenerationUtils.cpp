#include <bits/stdc++.h>
using namespace std;
using namespace chrono;
string str[5]={"EFGHIJKABCCDDEFGHIJKAB", "ACEGIKBDFHHJJACEGIKBDF", "HKCFIADGJBBEEHKCFIADGJ", "DHAEIBFJCGGKKDHAEIBFJC", "KEJDICHBGAAFFKEJDICHBG"};
int A[5], B[5], C[5][3], D[5], E[5], X=100, Y=100;
double Z=100;
void calculate(int a[5], int b[5], int c[5][3], int d[5], int e[5])
{
    string s[5][3];
    for (int i=0; i<5; i++)
    {
        for (int j=0; j<3; j++)
        {
            if (c[i][j]==0)
            {
                s[i][j]=str[i];
                if (d[i])
                    for (int k=0; k<22; k+=2)
                        if (k!=10)
                            swap(s[i][j][k], s[i][j][k+1]);
            }
            else if (c[i][j]==1)
            {
                s[i][j]=str[i].substr(12, 10)+str[i].substr(11, 1)+str[i].substr(10, 1)+str[i].substr(0, 10);
                if (d[i])
                    for (int k=0; k<22; k+=2)
                        if (k!=10)
                            swap(s[i][j][k], s[i][j][k+1]);
            }
            else
            {
                if (e[i]<2)
                    s[i][j]=str[b[i]];
                else
                    s[i][j]=str[b[i]].substr(12, 10)+str[b[i]].substr(11, 1)+str[b[i]].substr(10, 1)+str[b[i]].substr(0, 10);
                if (e[i]&1)
                    for (int k=0; k<22; k+=2)
                        if (k!=10)
                            swap(s[i][j][k], s[i][j][k+1]);
                int shift=(a[b[i]]-(s[i][j][10+e[i]/2]-'A')+11)%11;
                for (int k=0; k<22; k++)
                    s[i][j][k]=(s[i][j][k]-'A'+shift)%11+'A';
                for (int k=0; k<10; k++)
                {
                    if (s[i][j][k]==str[i][10+d[i]])
                    {
                        if (k&1)
                            swap(s[i][j][k-1], s[i][j][k]);
                        break;
                    }
                }
                for (int k=12; k<22; k++)
                {
                    if (s[i][j][k]==str[i][11-d[i]])
                    {
                        if (k%2==0)
                            swap(s[i][j][k], s[i][j][k+1]);
                        break;
                    }
                }
            }
        }
    }
    int cntX[11][3][2]={}, cntY[11][2]={}, cntZ[11]={}, x=0, y=0, ok=1;
    double z=0;
    for (int i=0; i<5; i++)
        for (int j=0; j<3; j++)
            for (int k=0; k<22; k++)
                cntX[s[i][j][k]-'A'][j][k&1]++;
    for (int i=0; i<11; i++)
    {
        int sum0=0, sum1=0;
        for (int j=0; j<3; j++)
        {
            x+=abs(cntX[i][j][0]-cntX[i][j][1]);
            sum0+=cntX[i][j][0];
            sum1+=cntX[i][j][1];
        }
        if (sum0!=sum1)
        {
            ok=0;
            break;
        }
    }
    if (!ok)
        return;
    for (int i=0; i<5; i++)
    {
        for (int j=0; j<3; j++)
        {
            cntY[s[i][j][10]-'A'][0]++;
            cntY[s[i][j][11]-'A'][1]++;
        }
    }
    for (int i=0; i<11; i++)
        y+=abs(cntY[i][0]-cntY[i][1]);
    for (int i=0; i<5; i++)
    {
        for (int j=0; j<3; j++)
        {
            for (int k=0; k<10; k+=2)
                cntZ[s[i][j][k]-'A']+=i*2+1;
            for (int k=10; k<22; k+=2)
                cntZ[s[i][j][k]-'A']+=i*2+2;
        }
    }
    for (int i=0; i<11; i++)
        z+=abs(1-4.0/330*cntZ[i]);
    if (X+Y+Z>x+y+z)
    {
        X=x, Y=y, Z=z;
        for (int i=0; i<5; i++)
            A[i]=a[i], B[i]=b[i], C[i][0]=c[i][0], C[i][1]=c[i][1], C[i][2]=c[i][2], D[i]=d[i], E[i]=e[i];
        cout << X << ' ' << Y << ' ' << Z << '\n';
        for (int i=0; i<5; i++)
            cout << A[i] << ' ';
        cout << '\n';
        for (int i=0; i<5; i++)
            cout << B[i] << ' ';
        cout << '\n';
        for (int i=0; i<5; i++)
        {
            for (int j=0; j<3; j++)
                cout << C[i][j] << ' ';
            cout << '\n';
        }
        for (int i=0; i<5; i++)
            cout << D[i] << ' ';
        cout << '\n';
        for (int i=0; i<5; i++)
            cout << E[i] << ' ';
        cout << '\n';
        for (int i=0; i<5; i++)
        {
            for (int j=0; j<5; j++)
                cout << s[i][0][j*2] << s[i][0][j*2+1] << ' ' << s[i][1][j*2] << s[i][1][j*2+1] << ' ' << s[i][2][j*2] << s[i][2][j*2+1] << '\n';
            cout << '\n';
            for (int j=5; j<6; j++)
                cout << s[i][0][j*2] << s[i][0][j*2+1] << ' ' << s[i][1][j*2] << s[i][1][j*2+1] << ' ' << s[i][2][j*2] << s[i][2][j*2+1] << '\n';
            cout << '\n';
            for (int j=6; j<11; j++)
                cout << s[i][0][j*2] << s[i][0][j*2+1] << ' ' << s[i][1][j*2] << s[i][1][j*2+1] << ' ' << s[i][2][j*2] << s[i][2][j*2+1] << '\n';
            cout << '\n';
        }
        for (int i=0; i<11; i++)
        {
            for (int j=0; j<3; j++)
                cout << cntX[i][j][0] << ' ' << cntX[i][j][1] << ' ';
            cout << '\n';
        }
    }
}
void iterate5(int a[5], int b[5], int c[5][3], int d[5])
{
    int e[5];
    for (e[0]=0; e[0]<4; e[0]++)
        for (e[1]=0; e[1]<4; e[1]++)
            for (e[2]=0; e[2]<4; e[2]++)
                for (e[3]=0; e[3]<4; e[3]++)
                    for (e[4]=0; e[4]<4; e[4]++)
                        calculate(a, b, c, d, e);
}
void iterate4(int a[5], int b[5], int c[5][3])
{
    int d[5];
    for (d[0]=0; d[0]<2; d[0]++)
        for (d[1]=0; d[1]<2; d[1]++)
            for (d[2]=0; d[2]<2; d[2]++)
                for (d[3]=0; d[3]<2; d[3]++)
                    for (d[4]=0; d[4]<2; d[4]++)
                        iterate5(a, b, c, d);
}
void iterate3(int a[5], int b[5])
{
    int c[5][3]={{0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
    do do do iterate4(a, b, c);
    while (next_permutation(c[4], c[4]+3));
    while (next_permutation(c[3], c[3]+3));
    while (next_permutation(c[2], c[2]+3));
//    while (next_permutation(c[1], c[1]+3));
//    while (next_permutation(c[0], c[0]+3));
}
int check2(int a[5], int b[5])
{
    for (int i=0; i<5; i++)
        if (b[i]==i || str[i][10]-'A'==a[b[i]] || str[i][10]-'A'==(a[b[i]]+b[i]+1)%11 || str[i][11]-'A'==a[b[i]] || str[i][11]-'A'==(a[b[i]]+b[i]+1)%11)
            return 0;
    return 1;
}
void iterate2(int a[5])
{
    int b[5]={0, 1, 2, 3, 4};
    do if (check2(a, b))
            iterate3(a, b);
    while (next_permutation(b, b+5));
}
int check1(int a[5])
{
    int aa[10]={a[0], a[1], a[2], a[3], a[4], (a[0]+1)%11, (a[1]+2)%11, (a[2]+3)%11, (a[3]+4)%11, (a[4]+5)%11}, cnt=0;
    sort(aa, aa+10);
    for (int i=0; i<10; i++)
    {
        if (i && aa[i-1]==aa[i] && aa[i]!=8)
            return 0;
        if (aa[i]==8)
            cnt++;
    }
    return cnt==2 || cnt==3;
}
void iterate1()
{
    int a[5];
    for (a[0]=7; a[0]<11; a[0]++)
    {
        for (a[1]=0; a[1]<11; a[1]++)
        {
            cout << "a: " << a[0] << ' ' << a[1] << '\n';
            for (a[2]=0; a[2]<11; a[2]++)
                for (a[3]=0; a[3]<11; a[3]++)
                    for (a[4]=0; a[4]<11; a[4]++)
                        if (check1(a))
                            iterate2(a);
        }
    }
}
int main()
{
    auto start=high_resolution_clock::now();
    iterate1();
    auto stop=high_resolution_clock::now();
    auto duration=duration_cast<microseconds>(stop-start);
    cout << "Duration: " << duration.count() << endl;

/*    int a[5]={0, 5, 3, 4, 8};
    int b[5]={3, 4, 1, 0, 2};
    int c[5][3]={{0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
    int d[5]={0, 1, 1, 0, 0};
    int e[5]={2, 2, 0, 0, 0};
    calculate(a, b, c, d, e); */

/*    int a[5]={0, 5, 10, 4, 3};
    int b[5]={1, 3, 4, 0, 2};
    int c[5][3]={{0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}, {0, 1, 2}};
    int d[5]={0, 0, 0, 0, 1};
    int e[5]={0, 3, 3, 3, 3};
    calculate(a, b, c, d, e); */
}
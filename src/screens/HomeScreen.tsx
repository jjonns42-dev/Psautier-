import React, { useEffect, useState } from 'react';
import {
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { getNextOffice, getOfficeByHour, type OfficeHour } from '../data/offices';
import { Colors } from '../theme/colors';

interface HomeScreenProps {
  navigation: any;
}

function getGreeting(hour: number): string {
  if (hour >= 5 && hour < 12) return 'Béni soit ce matin';
  if (hour >= 12 && hour < 18) return 'Béni soit ce jour';
  if (hour >= 18 && hour < 21) return 'Béni soit ce soir';
  return 'Bénie soit cette nuit';
}

function getDayOfWeek(): string {
  const days = ['Dimanche', 'Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi'];
  const months = [
    'janvier', 'février', 'mars', 'avril', 'mai', 'juin',
    'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre',
  ];
  const now = new Date();
  return `${days[now.getDay()]} ${now.getDate()} ${months[now.getMonth()]} ${now.getFullYear()}`;
}

export default function HomeScreen({ navigation }: HomeScreenProps) {
  const [currentHour, setCurrentHour] = useState(new Date().getHours());
  const [currentOffice, setCurrentOffice] = useState<OfficeHour | null>(null);
  const [nextOffice, setNextOffice] = useState<OfficeHour | null>(null);

  useEffect(() => {
    const now = new Date().getHours();
    setCurrentHour(now);
    setCurrentOffice(getOfficeByHour(now));
    setNextOffice(getNextOffice(now));

    const timer = setInterval(() => {
      const h = new Date().getHours();
      setCurrentHour(h);
      setCurrentOffice(getOfficeByHour(h));
      setNextOffice(getNextOffice(h));
    }, 60_000);
    return () => clearInterval(timer);
  }, []);

  if (!currentOffice || !nextOffice) return null;

  return (
    <SafeAreaView style={styles.safe} edges={['top']}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.blue} />
      <ScrollView contentContainerStyle={styles.container} showsVerticalScrollIndicator={false}>
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.headerSubtitle}>Liturgie des Heures</Text>
          <Text style={styles.headerTitle}>Byzantine</Text>
          <Text style={styles.headerDate}>{getDayOfWeek()}</Text>
        </View>

        {/* Greeting */}
        <View style={styles.greetingBox}>
          <Text style={styles.greetingText}>{getGreeting(currentHour)}</Text>
          <Text style={styles.greetingQuote}>
            « Venez, adorons et prosternons-nous ; pleurons devant le Seigneur qui nous a créés. »
          </Text>
          <Text style={styles.greetingRef}>— Psaume 94</Text>
        </View>

        {/* Office courant */}
        <Text style={styles.sectionLabel}>Office du moment</Text>
        <TouchableOpacity
          style={styles.currentOfficeCard}
          onPress={() => navigation.navigate('Office', { office: currentOffice })}
          activeOpacity={0.85}
        >
          <View style={styles.currentOfficeHeader}>
            <View>
              <Text style={styles.currentOfficeName}>{currentOffice.name}</Text>
              <Text style={styles.currentOfficeGreek}>{currentOffice.nameGreek}</Text>
            </View>
            <View style={styles.currentOfficeBadge}>
              <Text style={styles.currentOfficeBadgeText}>{currentOffice.nameFr}</Text>
            </View>
          </View>
          <Text style={styles.currentOfficeDesc} numberOfLines={3}>
            {currentOffice.description}
          </Text>
          <View style={styles.currentOfficeCTA}>
            <Text style={styles.currentOfficeCTAText}>Prier cet office →</Text>
          </View>
        </TouchableOpacity>

        {/* Tropaire */}
        <View style={styles.tropaire}>
          <Text style={styles.tropairLabel}>Tropaire</Text>
          <Text style={styles.tropairText} numberOfLines={4}>
            {currentOffice.tropaire}
          </Text>
        </View>

        {/* Prochain office */}
        <Text style={styles.sectionLabel}>Prochain office</Text>
        <TouchableOpacity
          style={styles.nextOfficeCard}
          onPress={() => navigation.navigate('Office', { office: nextOffice })}
          activeOpacity={0.85}
        >
          <Text style={styles.nextOfficeName}>{nextOffice.name}</Text>
          <Text style={styles.nextOfficeGreek}>{nextOffice.nameGreek}</Text>
          <Text style={styles.nextOfficeFr}>
            {nextOffice.nameFr} · {String(nextOffice.hour).padStart(2, '0')}h00
          </Text>
        </TouchableOpacity>

        {/* Psaumes du jour */}
        <Text style={styles.sectionLabel}>Psaumes de l'office</Text>
        {currentOffice.psaumes.map((psaume) => (
          <View key={psaume.numero} style={styles.psaumeCard}>
            <Text style={styles.psaumeNumero}>Psaume {psaume.numero}</Text>
            <Text style={styles.psaumeTitre}>{psaume.titre}</Text>
            <Text style={styles.psaumeTexte} numberOfLines={5}>
              {psaume.texte}
            </Text>
          </View>
        ))}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: Colors.blue,
  },
  container: {
    paddingBottom: 32,
  },
  header: {
    backgroundColor: Colors.blue,
    paddingHorizontal: 20,
    paddingTop: 16,
    paddingBottom: 28,
  },
  headerSubtitle: {
    color: Colors.goldLight,
    fontSize: 13,
    letterSpacing: 2,
    textTransform: 'uppercase',
    fontWeight: '600',
  },
  headerTitle: {
    color: Colors.white,
    fontSize: 32,
    fontWeight: '800',
    letterSpacing: 1,
    marginTop: 2,
  },
  headerDate: {
    color: 'rgba(255,255,255,0.7)',
    fontSize: 13,
    marginTop: 4,
  },
  greetingBox: {
    backgroundColor: Colors.burgundy,
    marginHorizontal: 16,
    marginTop: -10,
    borderRadius: 16,
    padding: 18,
    shadowColor: Colors.dark,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.2,
    shadowRadius: 8,
    elevation: 6,
  },
  greetingText: {
    color: Colors.goldLight,
    fontSize: 14,
    fontWeight: '700',
    letterSpacing: 1,
    textTransform: 'uppercase',
    marginBottom: 8,
  },
  greetingQuote: {
    color: Colors.white,
    fontSize: 14,
    fontStyle: 'italic',
    lineHeight: 20,
  },
  greetingRef: {
    color: 'rgba(255,255,255,0.6)',
    fontSize: 12,
    marginTop: 6,
    textAlign: 'right',
  },
  sectionLabel: {
    color: Colors.textSecondary,
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1.5,
    textTransform: 'uppercase',
    marginHorizontal: 20,
    marginTop: 24,
    marginBottom: 8,
  },
  currentOfficeCard: {
    backgroundColor: Colors.gold,
    marginHorizontal: 16,
    borderRadius: 16,
    padding: 18,
    shadowColor: Colors.goldDark,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
    elevation: 6,
  },
  currentOfficeHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    marginBottom: 10,
  },
  currentOfficeName: {
    color: Colors.dark,
    fontSize: 22,
    fontWeight: '800',
  },
  currentOfficeGreek: {
    color: Colors.darkMedium,
    fontSize: 13,
    fontStyle: 'italic',
    marginTop: 2,
  },
  currentOfficeBadge: {
    backgroundColor: Colors.dark,
    borderRadius: 10,
    paddingHorizontal: 10,
    paddingVertical: 4,
  },
  currentOfficeBadgeText: {
    color: Colors.gold,
    fontSize: 11,
    fontWeight: '700',
  },
  currentOfficeDesc: {
    color: Colors.darkMedium,
    fontSize: 13,
    lineHeight: 20,
  },
  currentOfficeCTA: {
    marginTop: 12,
    alignSelf: 'flex-end',
  },
  currentOfficeCTAText: {
    color: Colors.dark,
    fontWeight: '700',
    fontSize: 14,
  },
  tropaire: {
    backgroundColor: Colors.cream,
    marginHorizontal: 16,
    borderRadius: 12,
    padding: 16,
    borderLeftWidth: 4,
    borderLeftColor: Colors.burgundy,
    marginTop: 12,
  },
  tropairLabel: {
    color: Colors.burgundy,
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1,
    textTransform: 'uppercase',
    marginBottom: 8,
  },
  tropairText: {
    color: Colors.textPrimary,
    fontSize: 14,
    fontStyle: 'italic',
    lineHeight: 22,
  },
  nextOfficeCard: {
    backgroundColor: Colors.white,
    marginHorizontal: 16,
    borderRadius: 12,
    padding: 16,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  nextOfficeName: {
    color: Colors.textPrimary,
    fontSize: 18,
    fontWeight: '700',
  },
  nextOfficeGreek: {
    color: Colors.textSecondary,
    fontSize: 12,
    fontStyle: 'italic',
    marginTop: 2,
  },
  nextOfficeFr: {
    color: Colors.textSecondary,
    fontSize: 13,
    marginTop: 4,
  },
  psaumeCard: {
    backgroundColor: Colors.white,
    marginHorizontal: 16,
    marginBottom: 8,
    borderRadius: 12,
    padding: 16,
    borderWidth: 1,
    borderColor: Colors.border,
  },
  psaumeNumero: {
    color: Colors.gold,
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 1,
    textTransform: 'uppercase',
  },
  psaumeTitre: {
    color: Colors.textPrimary,
    fontSize: 16,
    fontWeight: '700',
    marginTop: 2,
    marginBottom: 8,
  },
  psaumeTexte: {
    color: Colors.textSecondary,
    fontSize: 14,
    lineHeight: 22,
    fontStyle: 'italic',
  },
});

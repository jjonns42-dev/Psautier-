import React, { useEffect, useState } from 'react';
import { ScrollView, StatusBar, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import HourCard from '../components/HourCard';
import { OFFICES, getOfficeByHour } from '../data/offices';
import { Colors } from '../theme/colors';

interface HoursListScreenProps {
  navigation: any;
}

export default function HoursListScreen({ navigation }: HoursListScreenProps) {
  const [currentOfficeId, setCurrentOfficeId] = useState<string>('');

  useEffect(() => {
    const update = () => {
      const office = getOfficeByHour(new Date().getHours());
      setCurrentOfficeId(office.id);
    };
    update();
    const timer = setInterval(update, 60_000);
    return () => clearInterval(timer);
  }, []);

  return (
    <SafeAreaView style={styles.safe} edges={['top']}>
      <StatusBar barStyle="light-content" backgroundColor={Colors.blue} />
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Les Heures</Text>
        <Text style={styles.headerSubtitle}>Horologion Byzantine</Text>
      </View>
      <ScrollView
        contentContainerStyle={styles.list}
        showsVerticalScrollIndicator={false}
      >
        <Text style={styles.intro}>
          La Liturgie des Heures Byzantine sanctifie chaque moment de la journée par la prière,
          suivant l'exemple des moines du mont Athos et des monastères orientaux.
        </Text>
        {OFFICES.map((office) => (
          <HourCard
            key={office.id}
            office={office}
            isCurrent={office.id === currentOfficeId}
            onPress={() => navigation.navigate('Office', { office })}
          />
        ))}
        <View style={styles.footer}>
          <Text style={styles.footerText}>
            ✝  Κύριε ἐλέησον  ✝
          </Text>
          <Text style={styles.footerSubtext}>Seigneur, aie pitié</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: Colors.cream,
  },
  header: {
    backgroundColor: Colors.blue,
    paddingHorizontal: 20,
    paddingVertical: 20,
  },
  headerTitle: {
    color: Colors.white,
    fontSize: 28,
    fontWeight: '800',
    letterSpacing: 0.5,
  },
  headerSubtitle: {
    color: Colors.goldLight,
    fontSize: 13,
    marginTop: 2,
    letterSpacing: 1,
  },
  list: {
    paddingVertical: 12,
    paddingBottom: 40,
  },
  intro: {
    color: Colors.textSecondary,
    fontSize: 13,
    lineHeight: 20,
    marginHorizontal: 20,
    marginBottom: 16,
    fontStyle: 'italic',
  },
  footer: {
    alignItems: 'center',
    marginTop: 24,
    marginBottom: 8,
  },
  footerText: {
    color: Colors.gold,
    fontSize: 16,
    fontWeight: '700',
    letterSpacing: 2,
  },
  footerSubtext: {
    color: Colors.textSecondary,
    fontSize: 12,
    marginTop: 4,
    fontStyle: 'italic',
  },
});
